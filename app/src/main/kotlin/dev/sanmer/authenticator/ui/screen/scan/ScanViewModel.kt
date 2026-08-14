package dev.sanmer.authenticator.ui.screen.scan

import android.Manifest
import android.content.Context
import android.net.Uri
import android.util.Size
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.TorchState
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import dev.sanmer.auth.OtpUri.Default.isOtpUri
import dev.sanmer.auth.QRCode
import dev.sanmer.authenticator.Logger
import dev.sanmer.authenticator.compat.PermissionCompat
import kotlinx.coroutines.awaitCancellation
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ScanViewModel(
    private val onUri: (Uri) -> Unit
) : ViewModel() {
    var isAllowed by mutableStateOf(false)
        private set

    var surfaceRequest by mutableStateOf<SurfaceRequest?>(null)
        private set

    private val preview = Preview.Builder()
        .setResolutionSelector(
            ResolutionSelector.Builder()
                .setResolutionStrategy(ResolutionStrategy.HIGHEST_AVAILABLE_STRATEGY)
                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
                .build()
        )
        .build()
        .apply {
            setSurfaceProvider { surfaceRequest = it }
        }

    private val imageAnalysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
        .build()
        .apply {
            setAnalyzer(Executors.newSingleThreadExecutor()) { image ->
                try {
                    val plane = image.planes.first()
                    val data = plane.buffer.asByteArray()
                    val content = QRCode.decodeFromYuv(
                        yuvData = data,
                        dataWidth = plane.rowStride,
                        dataHeight = image.height,
                        width = image.width,
                        height = image.height,
                    )
                    val uri = Uri.parse(content)
                    if (uri.isOtpUri()) onUri(uri)
                } catch (_: Throwable) {

                } finally {
                    image.close()
                }
            }
        }

    private var camera: Camera? = null
    var torchEnabled by mutableStateOf(false)
        private set

    private val logger = Logger.Android("ScanViewModel")

    init {
        logger.d("init")
    }

    fun isAllowed(context: Context) = if (isAllowed) {
        true
    } else {
        isAllowed = PermissionCompat.checkPermission(context, Manifest.permission.CAMERA)
        isAllowed
    }

    fun requestPermission(context: Context) =
        PermissionCompat.requestPermission(context, Manifest.permission.CAMERA) {
            isAllowed = it
        }

    suspend fun bindCamera(context: Context, lifecycleOwner: LifecycleOwner) {
        val processCameraProvider = ProcessCameraProvider.awaitInstance(context)
        val camera = processCameraProvider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview, imageAnalysis
        ).also { camera = it }

        camera.cameraInfo.torchState.observe(lifecycleOwner) {
            torchEnabled = it == TorchState.ON
        }

        try {
            awaitCancellation()
        } finally {
            camera.cameraInfo.torchState.removeObservers(lifecycleOwner)
            processCameraProvider.unbindAll()
        }
    }

    fun setTorch(enabled: Boolean) {
        val camera = camera ?: return
        camera.cameraControl.enableTorch(enabled)
    }

    fun setFocus(resolution: Size, offset: Offset) {
        val camera = camera ?: return
        val point = SurfaceOrientedMeteringPointFactory(
            resolution.width.toFloat(),
            resolution.height.toFloat()
        ).createPoint(offset.x, offset.y)
        camera.cameraControl.startFocusAndMetering(
            FocusMeteringAction.Builder(
                point, FocusMeteringAction.FLAG_AF or FocusMeteringAction.FLAG_AE
            ).setAutoCancelDuration(3, TimeUnit.SECONDS).build()
        )
    }

    fun setZoom(zoom: Float) {
        val camera = camera ?: return
        val zoomState = camera.cameraInfo.zoomState.value ?: return
        val newRatio = (zoomState.zoomRatio * zoom).coerceIn(
            zoomState.minZoomRatio,
            zoomState.maxZoomRatio
        )
        camera.cameraControl.setZoomRatio(newRatio)
    }

    fun fromImage(context: Context, uri: Uri) {
        runCatching {
            val cr = context.contentResolver
            val stream = cr.openInputStream(uri) ?: return
            val content = stream.use(QRCode::decodeFromStream) ?: return
            val uri = Uri.parse(content)
            if (uri.isOtpUri()) onUri(uri)
        }.onFailure {
            logger.e(it)
        }
    }

    private fun ByteBuffer.asByteArray(): ByteArray {
        rewind()
        val dst = ByteArray(remaining())
        get(dst)
        return dst
    }
}