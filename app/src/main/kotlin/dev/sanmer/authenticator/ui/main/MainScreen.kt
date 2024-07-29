package dev.sanmer.authenticator.ui.main

import android.net.Uri
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.sanmer.attestation.KeyAttestation
import dev.sanmer.authenticator.BuildConfig
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ui.component.BottomCornerLabel
import dev.sanmer.authenticator.ui.screens.edit.EditScreen
import dev.sanmer.authenticator.ui.screens.encode.EncodeScreen
import dev.sanmer.authenticator.ui.screens.home.HomeScreen
import dev.sanmer.authenticator.ui.screens.scan.ScanScreen
import dev.sanmer.authenticator.ui.screens.trash.TrashScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun MainScreen() {
    var isUntrusted by remember { mutableStateOf(false) }
    val navController = rememberNavController()

    LaunchedEffect(true) {
        isUntrusted = withContext(Dispatchers.IO) {
            KeyAttestation.getInstance(BuildConfig.APPLICATION_ID).isUntrusted
        }
    }

    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home()
        ) {
            Screen.Home(navController).addTo(this)
            Screen.Edit(navController).addTo(this)
            Screen.Scan(navController).addTo(this)
            Screen.Trash(navController).addTo(this)
            Screen.Encode(navController).addTo(this)
        }

        if (isUntrusted) {
            BottomCornerLabel(
                text = stringResource(id = R.string.untrusted),
                modifier = Modifier.align(Alignment.BottomEnd),
                width = 150.dp
            )
        }
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

        fun Edit(
            secret: String = " ",
            encode: Boolean = true
        ) = if (encode) "Edit/${Uri.encode(secret)}" else "Edit/${secret}"

        fun Scan() = "Scan"
        fun Trash() = "Trash"
        fun Encode() = "Encode"
    }

    class Home(navController: NavController) : Screen(
        route = Home(),
        content = { HomeScreen(navController = navController) }
    )

    class Edit(navController: NavController) : Screen(
        route = Edit("{secret}", false),
        content = { EditScreen(navController = navController) },
        arguments = listOf(navArgument("secret") { type = NavType.StringType })
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
}