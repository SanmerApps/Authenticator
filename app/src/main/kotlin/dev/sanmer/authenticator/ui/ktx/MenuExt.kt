package dev.sanmer.authenticator.ui.ktx

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun ProvideMenuShape(
    value: CornerBasedShape,
    content: @Composable () -> Unit
) = MaterialTheme(
    shapes = MaterialTheme.shapes.copy(extraSmall = value),
    content = content
)