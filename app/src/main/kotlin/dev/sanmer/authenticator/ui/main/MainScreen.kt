package dev.sanmer.authenticator.ui.main

import android.net.Uri
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.sanmer.authenticator.ui.screens.edit.EditScreen
import dev.sanmer.authenticator.ui.screens.encode.EncodeScreen
import dev.sanmer.authenticator.ui.screens.home.HomeScreen
import dev.sanmer.authenticator.ui.screens.ntp.NtpScreen
import dev.sanmer.authenticator.ui.screens.scan.ScanScreen
import dev.sanmer.authenticator.ui.screens.security.SecurityScreen
import dev.sanmer.authenticator.ui.screens.settings.SettingsScreen
import dev.sanmer.authenticator.ui.screens.trash.TrashScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home()
    ) {
        Screen.Home(navController).addTo(this)
        Screen.Settings(navController).addTo(this)
        Screen.Edit(navController).addTo(this)
        Screen.Scan(navController).addTo(this)
        Screen.Trash(navController).addTo(this)
        Screen.Encode(navController).addTo(this)
        Screen.Ntp(navController).addTo(this)
        Screen.Security(navController).addTo(this)
    }
}

sealed class Screen(
    private val route: String,
    private val content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
    private val arguments: List<NamedNavArgument> = emptyList(),
    private val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) = { fadeIn() },
    private val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = { fadeOut() },
) {
    fun addTo(builder: NavGraphBuilder) = builder.composable(
        route = this@Screen.route,
        arguments = this@Screen.arguments,
        enterTransition = this@Screen.enterTransition,
        exitTransition = this@Screen.exitTransition,
        content = this@Screen.content
    )

    @Suppress("FunctionName")
    companion object Routes {
        fun Home() = "Home"
        fun Settings() = "Settings"
        fun Edit(data: Any = " ", encode: Boolean = true) =
            if (encode) "Edit/${Uri.encode(data.toString())}" else "Edit/${data}"
        fun Scan() = "Scan"
        fun Trash() = "Trash"
        fun Encode() = "Encode"
        fun Ntp() = "Ntp"
        fun Security() = "Security"
    }

    class Home(navController: NavController) : Screen(
        route = Home(),
        content = { HomeScreen(navController = navController) }
    )

    class Settings(navController: NavController) : Screen(
        route = Settings(),
        content = { SettingsScreen(navController = navController) }
    )

    class Edit(navController: NavController) : Screen(
        route = Edit("{data}", false),
        content = { EditScreen(navController = navController) },
        arguments = listOf(
            navArgument("data") { type = NavType.StringType }
        )
    )

    class Scan(navController: NavController) : Screen(
        route = Scan(),
        content = { ScanScreen(navController = navController) }
    )

    class Trash(navController: NavController) : Screen(
        route = Trash(),
        content = { TrashScreen(navController = navController) }
    )

    class Encode(navController: NavController) : Screen(
        route = Encode(),
        content = { EncodeScreen(navController = navController) }
    )

    class Ntp(navController: NavController) : Screen(
        route = Ntp(),
        content = { NtpScreen(navController = navController) }
    )

    class Security(navController: NavController) : Screen(
        route = Security(),
        content = { SecurityScreen(navController = navController) }
    )
}