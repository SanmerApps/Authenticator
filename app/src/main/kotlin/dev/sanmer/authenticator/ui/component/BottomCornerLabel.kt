package dev.sanmer.authenticator.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.ui.theme.TrapezoidShape
import kotlin.math.sqrt

@Composable
fun BottomCornerLabel(
    text: String,
    modifier: Modifier = Modifier,
    width: Dp = 200.dp,
    height: Dp = 40.dp,
    containerColor: Color = MaterialTheme.colorScheme.errorContainer,
    contentColor: Color = MaterialTheme.colorScheme.onErrorContainer
) {
    val offset by remember {
        derivedStateOf {
            (width.value / 2f) - (sqrt(2f) / 4f) * (width.value - height.value)
        }
    }

    Box(
        modifier = modifier
            .size(size = width)
            .offset(x = offset.dp, y = offset.dp)
    ) {
        Box(
            modifier = Modifier
                .rotate(-45f)
                .align(Alignment.Center)
                .size(width = width, height = height)
                .background(
                    color = containerColor,
                    shape = TrapezoidShape(45f, true)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor,
            )
        }
    }
}