package dev.sanmer.authenticator.ui.screens.scan

import android.Manifest
import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.MeteringPoint
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import dev.sanmer.authenticator.Logger
import dev.sanmer.authenticator.compat.PermissionCompat
import dev.sanmer.qrcode.QRCode
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ScanViewModel(
    private val context: Application
) : ViewModel() {
    private val _isAllowed: Boolean
        get() = PermissionCompat.checkPermission(
            context, Manifest.permission.CAMERA
        )
    var isAllowed by mutableStateOf(_isAllowed)
        private set

    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest

    private val preview = Preview.Builder().build().apply {
        setSurfaceProvider { surfaceRequest ->
            _surfaceRequest.update { surfaceRequest }
        }
    }

    private val _text = MutableStateFlow("")
    val text = _text.asStateFlow()

    private val imageAnalysis = ImageAnalysis.Builder().apply {
        setBackgroundExecutor(Executors.newSingleThreadExecutor())
    }.build().apply {
        setAnalyzer(context.mainExecutor) { image ->
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
                _text.update { content }
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

    fun requestPermission(context: Context) {
        PermissionCompat.requestPermission(context, Manifest.permission.CAMERA) {
            isAllowed = it
        }
    }

    suspend fun bindToCamera(lifecycleOwner: LifecycleOwner) {
        val processCameraProvider = ProcessCameraProvider.awaitInstance(context)
        val camera = processCameraProvider.bindToLifecycle(
            lifecycleOwner, DEFAULT_BACK_CAMERA, preview, imageAnalysis
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

    fun enableTorch(enabled: Boolean) {
        val camera = camera ?: return
        camera.cameraControl.enableTorch(enabled)
    }

    fun startFocusAndMetering(focusPoint: MeteringPoint) {
        val camera = camera ?: return
        val action = FocusMeteringAction.Builder(
            focusPoint,
            FocusMeteringAction.FLAG_AF or FocusMeteringAction.FLAG_AE
        ).setAutoCancelDuration(3, TimeUnit.SECONDS).build()
        camera.cameraControl.startFocusAndMetering(action)
    }

    fun setZoomRatio(zoom: Float) {
        val camera = camera ?: return
        val zoomState = camera.cameraInfo.zoomState.value ?: return
        val newRatio = (zoomState.zoomRatio * zoom).coerceIn(
            zoomState.minZoomRatio,
            zoomState.maxZoomRatio
        )
        camera.cameraControl.setZoomRatio(newRatio)
    }

    fun rewind() {
        _text.update { "" }
    }

    fun scanImage(context: Context, uri: Uri) {
        runCatching {
            val cr = context.contentResolver
            checkNotNull(cr.openInputStream(uri)).use(QRCode::decodeFromStream)
        }.onSuccess { content ->
            content?.let { _text.update { content } }
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