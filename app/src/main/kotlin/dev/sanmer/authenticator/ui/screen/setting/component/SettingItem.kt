package dev.sanmer.authenticator.ui.screen.setting.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.ui.ktx.surface

@Composable
fun SettingItem(
    onClick: () -> Unit,
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable RowScope.() -> Unit = {},
    trailingIcon: @Composable RowScope. () -> Unit = {},
    shape: Shape = MaterialTheme.shapes.extraSmall
) = Row(
    modifier = modifier
        .surface(
            shape = shape,
            backgroundColor = MaterialTheme.colorScheme.surfaceContainer
        )
        .clickable(
            enabled = enabled,
            onClick = onClick
        )
        .padding(horizontal = 20.dp, vertical = 15.dp),
    horizontalArrangement = Arrangement.spacedBy(15.dp),
    verticalAlignment = Alignment.CenterVertically
) {
    leadingIcon()

    Column(
        modifier = Modifier
            .padding(start = 5.dp)
            .weight(1f)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }

    trailingIcon()
}