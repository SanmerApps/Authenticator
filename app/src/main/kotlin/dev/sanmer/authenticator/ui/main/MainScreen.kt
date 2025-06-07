package dev.sanmer.authenticator.ui.main

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.sanmer.authenticator.ui.screens.edit.EditScreen
import dev.sanmer.authenticator.ui.screens.encode.EncodeScreen
import dev.sanmer.authenticator.ui.screens.home.HomeScreen
import dev.sanmer.authenticator.ui.screens.ntp.NtpScreen
import dev.sanmer.authenticator.ui.screens.scan.ScanScreen
import dev.sanmer.authenticator.ui.screens.security.SecurityScreen
import dev.sanmer.authenticator.ui.screens.settings.SettingsScreen
import dev.sanmer.authenticator.ui.screens.trash.TrashScreen
import kotlinx.serialization.Serializable

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home
    ) {
        composable<Screen.Home> {
            HomeScreen(
                navController = navController
            )
        }

        composable<Screen.Settings> {
            SettingsScreen(
                navController = navController
            )
        }

        composable<Screen.Edit> {
            EditScreen(
                navController = navController
            )
        }

        composable<Screen.Scan> {
            ScanScreen(
                navController = navController
            )
        }

        composable<Screen.Trash> {
            TrashScreen(
                navController = navController
            )
        }

        composable<Screen.Encode> {
            EncodeScreen(
                navController = navController
            )
        }

        composable<Screen.Ntp> {
            NtpScreen(
                navController = navController
            )
        }

        composable<Screen.Security> {
            SecurityScreen(
                navController = navController
            )
        }
    }
}

sealed class Screen {
    @Serializable
    data object Home : Screen()

    @Serializable
    data object Settings : Screen()

    @Serializable
    data class Edit(
        val id: Long,
        val uri: String
    ) : Screen()

    @Serializable
    data object Scan : Screen()

    @Serializable
    data object Trash : Screen()

    @Serializable
    data object Encode : Screen()

    @Serializable
    data object Ntp : Screen()

    @Serializable
    data object Security : Screen()
}