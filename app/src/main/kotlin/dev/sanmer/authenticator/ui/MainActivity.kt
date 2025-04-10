package dev.sanmer.authenticator.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
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
                is LoadState.Locked -> AppTheme {
                    LockScreen()
                }

                is LoadState.Ready -> CompositionLocalProvider(
                    LocalPreference provides viewModel.preference
                ) {
                    AppTheme {
                        MainScreen()
                    }
                }
            }
        }
    }
}