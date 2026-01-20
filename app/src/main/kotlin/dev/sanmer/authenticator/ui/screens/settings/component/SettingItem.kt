package dev.sanmer.authenticator.ui.screens.settings.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.ui.component.Logo
import dev.sanmer.authenticator.ui.ktx.bottom
import dev.sanmer.authenticator.ui.ktx.surface

@Composable
fun SettingItem(
    icon: @Composable () -> Unit,
    title: String,
    text: String,
    onClick: () -> Unit
) = Row(
    modifier = Modifier
        .surface(
            shape = MaterialTheme.shapes.large,
            backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
            border = CardDefaults.outlinedCardBorder(false)
        )
        .clickable(onClick = onClick)
        .padding(all = 20.dp)
        .fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(20.dp),
    verticalAlignment = Alignment.CenterVertically
) {
    icon()

    Column(
        modifier = Modifier.weight(1f)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun SettingIcon(
    @DrawableRes icon: Int,
    color: Color
) = Logo(
    icon = icon,
    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    containerColor = color,
    modifier = Modifier.size(40.dp)
)

@Composable
fun SettingItem(
    @DrawableRes icon: Int,
    title: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) = Row(
    modifier = Modifier
        .surface(
            shape = MaterialTheme.shapes.medium,
            backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
            border = CardDefaults.outlinedCardBorder(false)
        )
        .clickable(enabled = enabled, onClick = onClick)
        .padding(all = 15.dp)
        .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp)
) {
    Icon(
        painter = painterResource(id = icon),
        contentDescription = null
    )

    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall
    )
}

@Composable
fun SettingBottomSheet(
    onDismiss: () -> Unit,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) = ModalBottomSheet(
    onDismissRequest = onDismiss,
    shape = MaterialTheme.shapes.large.bottom(0.dp),
    containerColor = MaterialTheme.colorScheme.surface
) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )

    Column(
        modifier = Modifier.padding(all = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        content = content
    )
}