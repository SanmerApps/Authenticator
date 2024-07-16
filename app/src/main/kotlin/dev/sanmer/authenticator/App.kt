package dev.sanmer.authenticator

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class App : Application() {
    init {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
    }

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