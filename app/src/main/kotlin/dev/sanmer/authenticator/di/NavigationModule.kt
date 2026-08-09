package dev.sanmer.authenticator.di

import androidx.navigation3.runtime.NavBackStack
import dev.sanmer.authenticator.ui.screen.Screen
import dev.sanmer.authenticator.ui.screen.brand.BrandScreen
import dev.sanmer.authenticator.ui.screen.edit.EditScreen
import dev.sanmer.authenticator.ui.screen.edit.EditViewModel
import dev.sanmer.authenticator.ui.screen.export.ExportScreen
import dev.sanmer.authenticator.ui.screen.export.ExportViewModel
import dev.sanmer.authenticator.ui.screen.home.HomeScreen
import dev.sanmer.authenticator.ui.screen.home.HomeViewModel
import dev.sanmer.authenticator.ui.screen.main.MainViewModel
import dev.sanmer.authenticator.ui.screen.ntp.NtpScreen
import dev.sanmer.authenticator.ui.screen.ntp.NtpViewModel
import dev.sanmer.authenticator.ui.screen.scan.ScanViewModel
import dev.sanmer.authenticator.ui.screen.setting.SettingScreen
import dev.sanmer.authenticator.ui.screen.setting.SettingViewModel
import dev.sanmer.authenticator.ui.screen.trash.TrashScreen
import dev.sanmer.authenticator.ui.screen.trash.TrashViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val Navigation = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::EditViewModel)
    viewModelOf(::ScanViewModel)
    viewModelOf(::SettingViewModel)
    viewModelOf(::TrashViewModel)
    viewModelOf(::NtpViewModel)
    viewModelOf(::ExportViewModel)

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
            EditScreen(
                viewModel = koinViewModel { parametersOf(it.authId, it.otpUri) },
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