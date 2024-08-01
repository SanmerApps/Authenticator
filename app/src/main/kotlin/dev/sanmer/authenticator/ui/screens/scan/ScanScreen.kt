package dev.sanmer.authenticator.ui.screens.scan

import androidx.camera.view.CameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ui.ktx.navigateSingleTopTo
import dev.sanmer.authenticator.ui.main.Screen
import dev.sanmer.authenticator.viewmodel.ScanViewModel
import dev.sanmer.otp.OtpUri.Default.isOtpUri

@Composable
fun ScanScreen(
    viewModel: ScanViewModel = hiltViewModel(),
    navController: NavController
) {
    val uri by viewModel.uri.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(uri) {
        if (uri.isOtpUri()) navController.navigateSingleTopTo(Screen.Edit(uri))
        onDispose { viewModel.rewind() }
    }

    DisposableEffect(true) {
        viewModel.bindToLifecycle(context, lifecycleOwner)
        onDispose { viewModel.unbind() }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CameraXPreview(
            controller = viewModel.cameraController,
            modifier = Modifier.fillMaxSize()
        )

        Crossfade(
            targetState = !viewModel.isAllowed || !viewModel.isShowing,
            animationSpec = tween(800),
            label = "CameraOff"
        ) { show ->
            if (show) {
                CameraOff(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        val contentPadding = WindowInsets.navigationBars.asPaddingValues()
        FloatingActionButton(
            onClick = { navController.navigateUp() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(contentPadding)
                .padding(all = 20.dp),
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrows_minimize),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun CameraOff(
    modifier: Modifier = Modifier
) = Box(
    modifier = modifier
        .background(
            color = MaterialTheme.colorScheme.surfaceContainer
        ),
    contentAlignment = Alignment.Center
) {
    Icon(
        painter = painterResource(id = R.drawable.camera_off),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(45.dp),
    )
}

@Composable
fun CameraXPreview(
    controller: CameraController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }
    LaunchedEffect(true) {
        previewView.controller = controller
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    )
}