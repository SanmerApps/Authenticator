package dev.sanmer.authenticator.ui.navigation.graphs

import android.net.Uri
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import dev.sanmer.authenticator.ui.navigation.MainScreen
import dev.sanmer.authenticator.ui.screens.home.HomeScreen
import dev.sanmer.authenticator.ui.screens.home.edit.EditScreen
import dev.sanmer.authenticator.ui.screens.home.scan.ScanScreen

enum class HomeScreen(val route: String) {
    Home("Home"),
    Edit("Edit/{secret}"),
    Scan("Scan");

    companion object {
        @Suppress("FunctionName")
        fun Edit(secret: String = " ") = "Edit/${Uri.encode(secret)}"
    }
}

fun NavGraphBuilder.homeScreen(
    navController: NavController
) = navigation(
    startDestination = HomeScreen.Home.route,
    route = MainScreen.Home.route
) {
    composable(
        route = HomeScreen.Home.route,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        HomeScreen(
            navController = navController
        )
    }

    composable(
        route = HomeScreen.Edit.route,
        arguments = listOf(navArgument("secret") { type = NavType.StringType }),
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        EditScreen(
            navController = navController
        )
    }

    composable(
        route = HomeScreen.Scan.route,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        ScanScreen(
            navController = navController
        )
    }
}