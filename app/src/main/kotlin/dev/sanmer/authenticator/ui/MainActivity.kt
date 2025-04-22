package dev.sanmer.authenticator.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import dev.sanmer.authenticator.ui.main.LockScreen
import dev.sanmer.authenticator.ui.main.MainScreen
import dev.sanmer.authenticator.ui.provider.LocalPreference
import dev.sanmer.authenticator.ui.theme.AppTheme
import dev.sanmer.authenticator.viewmodel.MainViewModel
import dev.sanmer.authenticator.viewmodel.MainViewModel.LoadState

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition { viewModel.isPending }

        setContent {
            when (viewModel.loadState) {
                LoadState.Pending -> {}
                else -> CompositionLocalProvider(
                    LocalPreference provides viewModel.preference
                ) {
                    AppTheme {
                        Crossfade(
                            modifier = Modifier.background(
                                color = MaterialTheme.colorScheme.background
                            ),
                            targetState = viewModel.isLocked,
                            animationSpec = tween(400)
                        ) { isLocked ->
                            if (isLocked) {
                                LockScreen()
                            } else {
                                MainScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}