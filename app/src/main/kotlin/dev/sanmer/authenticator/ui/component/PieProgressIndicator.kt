package dev.sanmer.authenticator.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun PieProgressIndicator(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    color: Color = ProgressIndicatorDefaults.circularColor,
    trackColor: Color = ProgressIndicatorDefaults.circularTrackColor
) {
    val coercedProgress = { progress().coerceIn(0f, 1f) }

    Canvas(
        modifier
            .semantics(mergeDescendants = true) {
                progressBarRangeInfo = ProgressBarRangeInfo(coercedProgress(), 0f..1f)
            }
            .size(40.dp)
    ) {
        val startAngle = 270f
        val sweep = coercedProgress() * 360f

        drawArc(
            color = trackColor,
            startAngle = startAngle,
            sweepAngle = 360f,
            useCenter = true
        )

        drawArc(
            color = color,
            startAngle = startAngle,
            sweepAngle = sweep,
            useCenter = true
        )
    }
}