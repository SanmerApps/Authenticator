package dev.sanmer.authenticator.ui.di

import androidx.navigation3.runtime.NavBackStack
import dev.sanmer.authenticator.ui.screens.Screen
import dev.sanmer.authenticator.ui.screens.edit.EditScreen
import dev.sanmer.authenticator.ui.screens.encode.EncodeScreen
import dev.sanmer.authenticator.ui.screens.home.HomeScreen
import dev.sanmer.authenticator.ui.screens.ntp.NtpScreen
import dev.sanmer.authenticator.ui.screens.scan.ScanScreen
import dev.sanmer.authenticator.ui.screens.security.SecurityScreen
import dev.sanmer.authenticator.ui.screens.settings.SettingsScreen
import dev.sanmer.authenticator.ui.screens.trash.TrashScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val Navigation = module {
    includes(ViewModels)

    activityRetainedScope {
        scoped { NavBackStack(Screen.Home) }

        navigation<Screen.Home> {
            val backStack = get<NavBackStack<Screen>>()
            HomeScreen(
                viewModel = koinViewModel(),
                goTo = backStack::add
            )
        }

        navigation<Screen.Settings> {
            val backStack = get<NavBackStack<Screen>>()
            SettingsScreen(
                viewModel = koinViewModel(),
                goTo = backStack::add,
                goBack = backStack::removeLastOrNull
            )
        }

        navigation<Screen.Edit> {
            val backStack = get<NavBackStack<Screen>>()
            EditScreen(
                viewModel = koinViewModel { parametersOf(it.id, it.uri) },
                goBack = backStack::removeLastOrNull
            )
        }

        navigation<Screen.Scan> {
            val backStack = get<NavBackStack<Screen>>()
            ScanScreen(
                viewModel = koinViewModel(),
                goTo = backStack::add,
                goBack = backStack::removeLastOrNull
            )
        }

        navigation<Screen.Encode> {
            val backStack = get<NavBackStack<Screen>>()
            EncodeScreen(
                viewModel = koinViewModel(),
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

        navigation<Screen.Security> {
            val backStack = get<NavBackStack<Screen>>()
            SecurityScreen(
                goBack = backStack::removeLastOrNull
            )
        }

        navigation<Screen.Trash> {
            val backStack = get<NavBackStack<Screen>>()
            TrashScreen(
                viewModel = koinViewModel(),
                goBack = backStack::removeLastOrNull
            )
        }
    }
}