package dev.sanmer.authenticator

import android.app.Application
import android.util.Log
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import dev.sanmer.authenticator.di.DataStore
import dev.sanmer.authenticator.di.Database
import dev.sanmer.authenticator.di.Repositories
import dev.sanmer.authenticator.di.ViewModels
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application(), CameraXConfig.Provider {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(DataStore, Database, Repositories, ViewModels)
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
            }.build()
}