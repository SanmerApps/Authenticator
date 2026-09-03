package dev.sanmer.authenticator.ui.screen.scan

import android.content.Context
import android.net.Uri
import android.util.Size
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.SurfaceRequest
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import dev.sanmer.authenticator.R

@Composable
fun ScanScreen(
    viewModel: ScanViewModel,
    goBack: () -> Unit
) {
    val context = LocalContext.current

    BackHandler(
        onBack = goBack
    )

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Crossfade(
            targetState = viewModel.isAllowed(context)
        ) { isAllowed ->
            if (isAllowed) CameraPreview(
                surfaceRequest = viewModel.surfaceRequest,
                onBind = viewModel::bindCamera,
                onFocusChange = viewModel::setFocus,
                onZoomChange = viewModel::setZoom
            ) else CameraOff(
                onClick = { viewModel.requestPermission(context) }
            )
        }

        BottomBar(
            onBack = goBack,
            onImage = viewModel::fromImage,
            torchEnabled = viewModel.torchEnabled,
            onTorchEnabledChange = viewModel::setTorch,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .alpha(0.8f)
        )
    }
}

@Composable
private fun CameraOff(
    onClick: () -> Unit
) = Icon(
    painter = painterResource(R.drawable.camera_slash),
    contentDescription = null,
    tint = MaterialTheme.colorScheme.onSurfaceVariant,
    modifier = Modifier
        .size(45.dp)
        .clickable(
            onClick = onClick,
            indication = ripple(bounded = false, radius = 45.dp),
            interactionSource = remember { MutableInteractionSource() }
        )
)

@Composable
private fun CameraPreview(
    surfaceRequest: SurfaceRequest?,
    onBind: suspend (Context, LifecycleOwner) -> Unit,
    onFocusChange: (Size, Offset) -> Unit,
    onZoomChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coordinateTransformer = remember { MutableCoordinateTransformer() }

    LaunchedEffect(lifecycleOwner) {
        onBind(context, lifecycleOwner)
    }

    surfaceRequest?.let {
        CameraXViewfinder(
            surfaceRequest = it,
            coordinateTransformer = coordinateTransformer,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        with(coordinateTransformer) {
                            onFocusChange(it.resolution, offset.transform())
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, zoom, _ ->
                        onZoomChange(zoom)
                    }
                }
        )
    }
}

@Composable
private fun BottomBar(
    onBack: () -> Unit,
    onImage: (Context, Uri) -> Unit,
    torchEnabled: Boolean,
    onTorchEnabledChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) = Row(
    modifier = modifier
        .windowInsetsPadding(WindowInsets.navigationBars)
        .padding(20.dp),
    horizontalArrangement = Arrangement.spacedBy(20.dp),
    verticalAlignment = Alignment.Bottom
) {
    val context = LocalContext.current
    val isPhotoPickerAvailable = remember { PickVisualMedia.isPhotoPickerAvailable(context) }
    val picker = rememberLauncherForActivityResult(PickVisualMedia()) {
        if (it != null) onImage(context, it)
    }

    if (isPhotoPickerAvailable) ActionButton(
        onClick = { picker.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly)) }
    ) {
        Icon(
            painter = painterResource(R.drawable.image_fill),
            contentDescription = null
        )
    }

    Spacer(modifier = Modifier.weight(1f))

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ActionButton(
            onClick = { onTorchEnabledChange(!torchEnabled) }
        ) {
            Icon(
                painter = painterResource(
                    if (torchEnabled) R.drawable.lightning_fill
                    else R.drawable.lightning_slash_fill
                ),
                contentDescription = null
            )
        }

        ActionButton(
            onClick = onBack
        ) {
            Icon(
                painter = painterResource(R.drawable.x),
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
    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
    shape = CircleShape,
    content = content
)