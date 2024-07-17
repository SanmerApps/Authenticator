package dev.sanmer.authenticator.ui.screens.scan

import androidx.camera.core.CameraState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ui.component.CameraXPreview
import dev.sanmer.authenticator.ui.ktx.navigateSingleTopTo
import dev.sanmer.authenticator.ui.ktx.surface
import dev.sanmer.authenticator.ui.main.Screen
import dev.sanmer.authenticator.viewmodel.ScanViewModel
import dev.sanmer.otp.OtpUri.Companion.isOtpAuthUri

@Composable
fun ScanScreen(
    viewModel: ScanViewModel = hiltViewModel(),
    navController: NavController
) {
    val uri by viewModel.uri.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(uri) {
        if (uri.isOtpAuthUri()) navController.navigateSingleTopTo(Screen.Edit(uri))
        onDispose { viewModel.rewind() }
    }

    DisposableEffect(true) {
        viewModel.bindToLifecycle(context, lifecycleOwner)
        onDispose { viewModel.unbindAll() }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(240.dp)
                .surface(
                    shape = MaterialTheme.shapes.medium,
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    border = CardDefaults.outlinedCardBorder()
                )
        ) {
            when {
                !viewModel.isAllowed -> {
                    CameraOff(
                        modifier = Modifier.fillMaxSize()
                    )
                }

                viewModel.cameraType == CameraState.Type.OPEN -> {
                    CameraXPreview(
                        preview = viewModel.preview,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
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