package dev.sanmer.authenticator.ui.screen.setting.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingColumn(
    modifier: Modifier = Modifier,
    title: String = "",
    content: @Composable (ColumnScope.() -> Unit)
) = Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(2.dp)
) {
    if (title.isNotEmpty()) Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(start = 20.dp, bottom = 8.dp)
    )

    content()
}