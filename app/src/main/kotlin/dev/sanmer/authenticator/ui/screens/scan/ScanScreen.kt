package dev.sanmer.authenticator.ui.screens.scan

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ui.screens.Screen
import dev.sanmer.otp.OtpUri.Default.isOtpUri

@Composable
fun ScanScreen(
    viewModel: ScanViewModel,
    goTo: (Screen) -> Unit,
    goBack: () -> Unit
) {
    val context = LocalContext.current
    val text by viewModel.text.collectAsStateWithLifecycle()

    DisposableEffect(text) {
        if (text.isOtpUri()) goTo(Screen.Edit(uri = text))
        onDispose { viewModel.rewind() }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Crossfade(
            targetState = viewModel.isAllowed,
            animationSpec = tween(600)
        ) { isAllowed ->
            if (isAllowed) {
                CameraXPreview(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                CameraOff(
                    onClick = { viewModel.requestPermission(context) },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        ActionButtons(
            modifier = Modifier.align(Alignment.BottomEnd),
            scanImage = viewModel::scanImage,
            onBack = goBack,
            torchEnabled = viewModel.torchEnabled,
            enableTorch = viewModel::enableTorch
        )
    }
}

@Composable
private fun CameraOff(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) = Box(
    modifier = modifier
        .background(
            color = MaterialTheme.colorScheme.surfaceContainer
        )
        .clickable(
            enabled = true,
            onClick = onClick
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
private fun CameraXPreview(
    viewModel: ScanViewModel,
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val coordinateTransformer = remember { MutableCoordinateTransformer() }
    val surfaceRequest by viewModel.surfaceRequest.collectAsStateWithLifecycle()

    LaunchedEffect(lifecycleOwner) {
        viewModel.bindToCamera(lifecycleOwner)
    }

    val request = surfaceRequest ?: return
    val meteringFactory by remember(request) {
        derivedStateOf {
            with(request.resolution) {
                SurfaceOrientedMeteringPointFactory(width.toFloat(), height.toFloat())
            }
        }
    }
    CameraXViewfinder(
        surfaceRequest = request,
        coordinateTransformer = coordinateTransformer,
        modifier = modifier
            .pointerInput(true) {
                detectTapGestures { offset ->
                    val surfacePoint = with(coordinateTransformer) {
                        offset.transform()
                    }
                    val focusPoint = meteringFactory.createPoint(
                        surfacePoint.x,
                        surfacePoint.y
                    )
                    viewModel.startFocusAndMetering(focusPoint)
                }
            }
            .pointerInput(true) {
                detectTransformGestures { _, _, zoom, _ ->
                    viewModel.setZoomRatio(zoom)
                }
            }
    )
}

@Composable
private fun ActionButtons(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    scanImage: (Context, Uri) -> Unit,
    torchEnabled: Boolean,
    enableTorch: (Boolean) -> Unit
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
        verticalAlignment = Alignment.Bottom
    ) {
        ActionButton(
            onClick = {
                pickImage.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.photo),
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ActionButton(
                onClick = { enableTorch(!torchEnabled) }
            ) {
                Icon(
                    painter = painterResource(id = if (torchEnabled) R.drawable.bolt else R.drawable.bolt_off),
                    contentDescription = null
                )
            }

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