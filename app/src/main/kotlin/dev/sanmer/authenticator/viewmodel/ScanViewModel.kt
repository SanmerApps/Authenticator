package dev.sanmer.authenticator.viewmodel

import android.Manifest
import android.app.Application
import android.content.Context
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraState
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.authenticator.compat.PermissionCompat
import dev.sanmer.authenticator.ktx.updateDistinct
import dev.sanmer.qrcode.QRCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application), ImageAnalysis.Analyzer {
    private val context: Context by lazy { getApplication() }
    private val _isAllowed: Boolean
        get() = PermissionCompat.checkPermission(
            context, Manifest.permission.CAMERA
        )

    var isAllowed by mutableStateOf(_isAllowed)
        private set

    val cameraController by lazy { LifecycleCameraController(context) }

    var cameraType by mutableStateOf(CameraState.Type.PENDING_OPEN)
        private set

    val isShowing: Boolean
        inline get() = cameraType == CameraState.Type.OPEN

    private val uriFlow = MutableStateFlow("")
    val uri get() = uriFlow.asStateFlow()

    init {
        Timber.d("ScanViewModel init")
    }

    private fun updateCameraInfo(cameraInfo: CameraInfo) {
        cameraInfo.cameraState.asFlow()
            .onEach {
                cameraType = it.type

            }.launchIn(viewModelScope)
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
            cameraController.cameraInfo?.let(::updateCameraInfo)
        }

    fun unbind() {
        cameraController.unbind()
    }

    fun rewind() {
        uriFlow.update { "" }
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

            uriFlow.updateDistinct { content }

        } catch (_: Throwable) {

        } finally {
            image.close()
        }
    }

    private fun ByteBuffer.asByteArray(): ByteArray {
        rewind()
        val dst = ByteArray(remaining())
        get(dst)
        return dst
    }
}