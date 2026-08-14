package dev.sanmer.authenticator.ui.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.isSpecified

@Composable
fun Point(
    modifier: Modifier,
    color: Color,
    ringColor: Color = Color.Unspecified,
    ringScale: Float = 1f,
    ringAlpha: Float = 1f
) = Canvas(modifier = modifier) {
    val radius = size.minDimension / 2f
    val innerRadius = radius * 0.6f

    drawCircle(
        color = color,
        radius = innerRadius,
        center = center
    )

    if (ringColor.isSpecified) {
        val ringRadius = (radius * ringScale + innerRadius) / 2
        val strokeWidth = radius * ringScale - innerRadius

        drawCircle(
            color = ringColor,
            alpha = ringAlpha,
            radius = ringRadius,
            center = center,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun AnimatedPoint(
    modifier: Modifier,
    color: Color,
    ringColor: Color = color,
) {
    val transition = rememberInfiniteTransition()
    val scale by transition.animateValue(
        initialValue = 0.1f,
        targetValue = 1f,
        typeConverter = Float.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        )
    )
    val alpha by transition.animateValue(
        initialValue = 0.15f,
        targetValue = 0.45f,
        typeConverter = Float.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        )
    )

    Point(
        modifier = modifier,
        color = color,
        ringColor = ringColor,
        ringScale = scale,
        ringAlpha = alpha
    )
}