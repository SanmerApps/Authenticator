package dev.sanmer.authenticator.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun IconRow(
    modifier: Modifier = Modifier,
    leadingIcon: @Composable RowScope.() -> Unit = { FixedBox() },
    trailingIcon: @Composable RowScope.() -> Unit = { FixedBox() },
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable RowScope.() -> Unit,
) = Row(
    modifier = modifier,
    horizontalArrangement = horizontalArrangement,
    verticalAlignment = verticalAlignment
) {
    leadingIcon()
    content()
    trailingIcon()
}

@Composable
fun FixedBox(
    content: @Composable (BoxScope.() -> Unit) = {}
) = Box(
    modifier = Modifier.size(48.dp),
    contentAlignment = Alignment.Center,
    content = content
)

@Composable
fun FixedIcon(
    painter: Painter,
    contentDescription: String? = null,
    tint: Color = LocalContentColor.current
) = FixedBox {
    Icon(
        painter = painter,
        contentDescription = contentDescription,
        tint = tint
    )
}