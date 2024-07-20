package dev.sanmer.authenticator

import android.app.Application
import android.util.Log
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class App : Application(), CameraXConfig.Provider {
    init {
        Timber.plant(Timber.DebugTree())
    }

    override fun getCameraXConfig() =
        CameraXConfig.Builder.fromConfig(Camera2Config.defaultConfig())
            .apply {
                if (BuildConfig.DEBUG) {
                    setMinimumLoggingLevel(Log.DEBUG)
                } else {
                    setMinimumLoggingLevel(Log.INFO)
                }
            }.build()
}