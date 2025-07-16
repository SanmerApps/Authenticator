package dev.sanmer.authenticator.ui.screens.scan

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import dev.sanmer.authenticator.Logger
import dev.sanmer.authenticator.annotation.ApplicationContext
import dev.sanmer.authenticator.compat.PermissionCompat
import dev.sanmer.qrcode.QRCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.nio.ByteBuffer
import java.util.concurrent.Executors

class ScanViewModel(
    @ApplicationContext private val context: Context
) : ViewModel(), ImageAnalysis.Analyzer {
    private val _isAllowed: Boolean
        get() = PermissionCompat.checkPermission(
            context, Manifest.permission.CAMERA
        )

    var isAllowed by mutableStateOf(_isAllowed)
        private set

    val cameraController by lazy { LifecycleCameraController(context) }

    private val _uri = MutableStateFlow("")
    val uri = _uri.asStateFlow()

    private val logger = Logger.Android("ScanViewModel")

    init {
        logger.d("init")
    }

    private fun requestPermission(context: Context, callback: () -> Unit) {
        PermissionCompat.requestPermission(context, Manifest.permission.CAMERA) {
            if (it) callback()
            isAllowed = it
        }
    }

    fun bindToLifecycle(context: Context, lifecycleOwner: LifecycleOwner) =
        requestPermission(context) {
            cameraController.previewResolutionSelector =
                ResolutionSelector.Builder()
                    .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
                    .build()

            cameraController.setEnabledUseCases(LifecycleCameraController.IMAGE_ANALYSIS)
            cameraController.setImageAnalysisBackgroundExecutor(Executors.newSingleThreadExecutor())
            cameraController.setImageAnalysisAnalyzer(context.mainExecutor, this)

            cameraController.bindToLifecycle(lifecycleOwner)
        }

    fun unbind() {
        cameraController.unbind()
    }

    fun rewind() {
        _uri.update { "" }
    }

    override fun analyze(image: ImageProxy) {
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

            _uri.update { content }

        } catch (_: Throwable) {

        } finally {
            image.close()
        }
    }

    fun scanImage(context: Context, uri: Uri) {
        runCatching {
            val cr = context.contentResolver
            checkNotNull(cr.openInputStream(uri)).use(QRCode::decodeFromStream)
        }.onSuccess { content ->
            _uri.update { content }
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