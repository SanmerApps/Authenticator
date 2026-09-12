package dev.sanmer.authenticator.di

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.metadata
import androidx.navigation3.runtime.result.LocalResultEventBus
import androidx.navigation3.runtime.result.ResultEffect
import androidx.navigation3.ui.NavDisplay
import dev.sanmer.authenticator.model.OtpUri
import dev.sanmer.authenticator.ui.screen.Screen
import dev.sanmer.authenticator.ui.screen.brand.BrandScreen
import dev.sanmer.authenticator.ui.screen.edit.EditScreen
import dev.sanmer.authenticator.ui.screen.edit.EditViewModel
import dev.sanmer.authenticator.ui.screen.export.ExportScreen
import dev.sanmer.authenticator.ui.screen.home.HomeScreen
import dev.sanmer.authenticator.ui.screen.ntp.NtpScreen
import dev.sanmer.authenticator.ui.screen.scan.ScanScreen
import dev.sanmer.authenticator.ui.screen.scan.ScanViewModel
import dev.sanmer.authenticator.ui.screen.setting.SettingScreen
import dev.sanmer.authenticator.ui.screen.trash.TrashScreen
import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val AppModule = module {
    includes(ViewModelsModule)

    activityRetainedScope {
        scoped { NavBackStack(Screen.Home) }

        navigation<Screen.Home> {
            val backStack = get<NavBackStack<Screen>>()
            HomeScreen(
                viewModel = koinViewModel(),
                goTo = backStack::add
            )
        }

        navigation<Screen.Edit> {
            val backStack = get<NavBackStack<Screen>>()
            val viewModel = koinViewModel<EditViewModel> { parametersOf(it.authId, it.otpUri) }

            ResultEffect<OtpUri> { otpUri ->
                viewModel.fromOtpUri(otpUri)
            }

            EditScreen(
                viewModel = viewModel,
                goTo = backStack::add,
                goBack = backStack::removeLastOrNull
            )
        }

        navigation<Screen.Scan>(
            metadata = metadata {
                val transitionSpec = fadeIn(
                    animationSpec = tween(500)
                ) togetherWith fadeOut(
                    animationSpec = tween(500)
                )
                put(NavDisplay.TransitionKey) { transitionSpec }
                put(NavDisplay.PopTransitionKey) { transitionSpec }
                put(NavDisplay.PredictivePopTransitionKey) { transitionSpec }
            }
        ) {
            val backStack = get<NavBackStack<Screen>>()
            val resultBus = LocalResultEventBus.current
            val viewModel = koinViewModel<ScanViewModel> {
                parametersOf(
                    ScanViewModel.Callback { otpUri ->
                        resultBus.sendResult(otpUri)
                        backStack.remove(Screen.Scan)
                    }
                )
            }

            ScanScreen(
                viewModel = viewModel,
                goBack = backStack::removeLastOrNull
            )
        }

        navigation<Screen.Setting> {
            val backStack = get<NavBackStack<Screen>>()
            SettingScreen(
                viewModel = koinViewModel(),
                goTo = backStack::add,
                goBack = backStack::removeLastOrNull
            )
        }

        navigation<Screen.Trash> {
            val backStack = get<NavBackStack<Screen>>()
            TrashScreen(
                viewModel = koinViewModel(),
                goTo = backStack::add,
                goBack = backStack::removeLastOrNull
            )
        }

        navigation<Screen.Ntp> {
            val backStack = get<NavBackStack<Screen>>()
            NtpScreen(
                viewModel = koinViewModel(),
                goBack = backStack::removeLastOrNull
            )
        }

        navigation<Screen.Export> {
            val backStack = get<NavBackStack<Screen>>()
            ExportScreen(
                viewModel = koinViewModel(),
                goBack = backStack::removeLastOrNull
            )
        }

        navigation<Screen.Brand> {
            val backStack = get<NavBackStack<Screen>>()
            BrandScreen(
                goBack = backStack::removeLastOrNull
            )
        }
    }
}