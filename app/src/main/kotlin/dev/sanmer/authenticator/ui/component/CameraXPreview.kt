package dev.sanmer.authenticator.ui.component

import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun CameraXPreview(
    preview: Preview,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }
    LaunchedEffect(preview) {
        preview.surfaceProvider = previewView.surfaceProvider
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    )
}