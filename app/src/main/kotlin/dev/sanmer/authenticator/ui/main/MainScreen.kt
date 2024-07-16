package dev.sanmer.authenticator.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dev.sanmer.attestation.KeyAttestation
import dev.sanmer.authenticator.BuildConfig
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ui.component.BottomCornerLabel
import dev.sanmer.authenticator.ui.navigation.MainScreen
import dev.sanmer.authenticator.ui.navigation.graphs.homeScreen
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
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets.navigationBars
        ) { innerPadding ->
            NavHost(
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                startDestination = MainScreen.Home.route
            ) {
                homeScreen(
                    navController = navController
                )
            }
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