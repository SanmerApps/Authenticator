package dev.sanmer.authenticator.ui

import android.app.ComponentCaller
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation3.runtime.NavBackStack
import dev.sanmer.authenticator.crypto.BiometricKey
import dev.sanmer.authenticator.datastore.compose.LocalPreference
import dev.sanmer.authenticator.ui.screen.Screen
import dev.sanmer.authenticator.ui.screen.main.MainScreen
import dev.sanmer.authenticator.ui.screen.main.MainViewModel
import dev.sanmer.authenticator.ui.screen.main.UnlockScreen
import dev.sanmer.authenticator.ui.theme.AppTheme
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.compose.navigation3.getEntryProvider
import org.koin.androidx.scope.activityRetainedScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.scope.Scope

@OptIn(KoinExperimentalAPI::class)
class MainActivity : ComponentActivity(), AndroidScopeComponent {
    override val scope: Scope by activityRetainedScope()
    private val viewModel by viewModel<MainViewModel>()
    private val backStack by inject<NavBackStack<Screen>>()

    private fun fromIntent(intent: Intent) {
        intent.data?.let {
            backStack.add(Screen.Edit(otpUri = it))
        }
    }

    private fun setSecureWindow(secure: Boolean) {
        if (secure) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    override fun onNewIntent(intent: Intent, caller: ComponentCaller) {
        fromIntent(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)

        BiometricKey.init(this)
        splashScreen.setKeepOnScreenCondition { viewModel.preference.isPending }
        fromIntent(intent)

        setContent {
            viewModel.preference.onSuccess { preference ->
                DisposableEffect(preference.secureWindow) {
                    setSecureWindow(preference.secureWindow)
                    onDispose {}
                }

                CompositionLocalProvider(
                    LocalPreference provides preference
                ) {
                    AppTheme {
                        AnimatedContent(
                            modifier = Modifier.background(MaterialTheme.colorScheme.background),
                            targetState = viewModel.isDecrypted(preference),
                            transitionSpec = {
                                fadeIn(
                                    animationSpec = tween(500)
                                ) togetherWith fadeOut(
                                    animationSpec = tween(500)
                                )
                            }
                        ) { isDecrypted ->
                            if (isDecrypted) {
                                MainScreen(
                                    backStack = backStack,
                                    entryProvider = getEntryProvider()
                                )
                            } else {
                                UnlockScreen(
                                    viewModel = viewModel,
                                    preference = preference
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}