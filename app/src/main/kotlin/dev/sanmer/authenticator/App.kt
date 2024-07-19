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
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
    }

    override fun getCameraXConfig() =
        CameraXConfig.Builder.fromConfig(Camera2Config.defaultConfig())
            .apply {
                if (BuildConfig.DEBUG) {
                    setMinimumLoggingLevel(Log.DEBUG)
                } else {
                    setMinimumLoggingLevel(Log.INFO)
                }
            }
            .build()

    class DebugTree : Timber.DebugTree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            super.log(priority, "<AUTH_DEBUG>$tag", message, t)
        }

        override fun createStackElementTag(element: StackTraceElement): String {
            return super.createStackElementTag(element) + "(L${element.lineNumber})"
        }
    }

    class ReleaseTree : Timber.DebugTree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            super.log(priority, "<AUTH_REL>$tag", message, t)
        }
    }
}