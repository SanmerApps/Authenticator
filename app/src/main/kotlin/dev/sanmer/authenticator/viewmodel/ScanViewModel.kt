package dev.sanmer.authenticator.viewmodel

import android.Manifest
import android.app.Application
import android.content.Context
import android.graphics.ImageFormat.YUV_420_888
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraState
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
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
import dev.sanmer.qrcode.QrCodeCompat
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

    private var cameraProviderOrNull: ProcessCameraProvider? = null
    private val cameraProvider: ProcessCameraProvider
        get() = checkNotNull(cameraProviderOrNull) {
            "CameraProvider haven't been received"
        }

    private val imageAnalyzer by lazy {
        ImageAnalysis.Builder()
            .build()
            .also {
                it.setAnalyzer(Executors.newSingleThreadExecutor(), this)
            }
    }

    val preview by lazy {
        Preview.Builder()
            .build()
    }

    var cameraType by mutableStateOf(CameraState.Type.CLOSED)
        private set

    private val uriFlow = MutableStateFlow("")
    val uri get() = uriFlow.asStateFlow()

    init {
        Timber.d("ScanViewModel init")
    }

    private fun getInstance(block: ProcessCameraProvider.() -> Unit) {
        ProcessCameraProvider.getInstance(context).apply {
            addListener({
                cameraProviderOrNull = get()
                block(cameraProvider)

            }, context.mainExecutor)
        }
    }

    private fun updateCameraControl(cameraControl: CameraControl) {
        cameraControl.setLinearZoom(0.6f)
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

    fun bindToLifecycle(context: Context, lifecycleOwner: LifecycleOwner) {
        requestPermission(context) {
            runCatching {
                getInstance {
                    val camera = bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalyzer
                    )

                    updateCameraControl(camera.cameraControl)
                    updateCameraInfo(camera.cameraInfo)
                }

            }.onFailure {
                Timber.e(it)
            }
        }
    }

    fun unbindAll() {
        runCatching {
            cameraProvider.unbindAll()
        }.onFailure {
            Timber.e(it)
        }
    }

    fun rewind() {
        uriFlow.update { "" }
    }

    override fun analyze(image: ImageProxy) {
        if (image.format != YUV_420_888) {
            image.close()
            return
        }

        try {
            val plane = image.planes.first()
            val data = plane.buffer.asByteArray()
            val content = QrCodeCompat.decodeFromYuv(
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