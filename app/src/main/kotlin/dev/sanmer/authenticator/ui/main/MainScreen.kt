package dev.sanmer.authenticator.ui.main

import android.net.Uri
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
import dev.sanmer.authenticator.ui.main.Screen.Companion.edit
import dev.sanmer.authenticator.ui.main.Screen.Companion.encode
import dev.sanmer.authenticator.ui.main.Screen.Companion.home
import dev.sanmer.authenticator.ui.main.Screen.Companion.scan
import dev.sanmer.authenticator.ui.main.Screen.Companion.trash
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
    LaunchedEffect(true) {
        isUntrusted = withContext(Dispatchers.IO) {
            KeyAttestation.getInstance(BuildConfig.APPLICATION_ID).isUntrusted
        }
    }

    val navController = rememberNavController()

    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route
        ) {
            home(navController)
            edit(navController)
            scan(navController)
            trash(navController)
            encode(navController)
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

enum class Screen(val route: String) {
    Home("Home"),
    Edit("Edit/{secret}"),
    Scan("Scan"),
    Trash("Trash"),
    Encode("Encode");

    companion object {
        @Suppress("FunctionName")
        fun Edit(secret: String = " ") = "Edit/${Uri.encode(secret)}"

        fun NavGraphBuilder.home(
            navController: NavController
        ) = composable(
            route = Home.route,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            HomeScreen(
                navController = navController
            )
        }

        fun NavGraphBuilder.edit(
            navController: NavController
        ) = composable(
            route = Edit.route,
            arguments = listOf(navArgument("secret") { type = NavType.StringType }),
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            EditScreen(
                navController = navController
            )
        }

        fun NavGraphBuilder.scan(
            navController: NavController
        ) = composable(
            route = Scan.route,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            ScanScreen(
                navController = navController
            )
        }

        fun NavGraphBuilder.trash(
            navController: NavController
        ) = composable(
            route = Trash.route,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            TrashScreen(
                navController = navController
            )
        }

        fun NavGraphBuilder.encode(
            navController: NavController
        ) = composable(
            route = Encode.route,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            EncodeScreen(
                navController = navController
            )
        }
    }
}