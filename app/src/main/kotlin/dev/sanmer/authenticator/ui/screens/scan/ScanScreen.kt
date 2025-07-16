package dev.sanmer.authenticator.ui.screens.scan

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.CameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
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
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ui.ktx.navigateSingleTopTo
import dev.sanmer.authenticator.ui.main.Screen
import dev.sanmer.otp.OtpUri.Default.isOtpUri
import org.koin.androidx.compose.koinViewModel

@Composable
fun ScanScreen(
    viewModel: ScanViewModel = koinViewModel(),
    navController: NavController
) {
    val uri by viewModel.uri.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(uri) {
        if (uri.isOtpUri()) navController.navigateSingleTopTo(Screen.Edit(-1, uri))
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
            targetState = !viewModel.isAllowed,
            animationSpec = tween(600)
        ) { show ->
            if (show) {
                CameraOff(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        ActionButtons(
            modifier = Modifier.align(Alignment.BottomEnd),
            scanImage = viewModel::scanImage,
            onBack = navController::navigateUp
        )
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

@Composable
private fun ActionButtons(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    scanImage: (Context, Uri) -> Unit
) {
    val context = LocalContext.current
    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { if (it != null) scanImage(context, it) }
    )

    Row(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(all = 20.dp),
    ) {
        ActionButton(
            onClick = {
                pickImage.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.photo),
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        ActionButton(
            onClick = onBack
        ) {
            Icon(
                painter = painterResource(id = R.drawable.x),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun ActionButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) = FloatingActionButton(
    onClick = onClick,
    containerColor = FloatingActionButtonDefaults.containerColor.copy(alpha = 0.8f),
    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
    shape = CircleShape,
    content = content
)