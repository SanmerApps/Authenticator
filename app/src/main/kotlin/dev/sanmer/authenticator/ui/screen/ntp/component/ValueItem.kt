package dev.sanmer.authenticator.ui.screen.ntp.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.ui.ktx.surface

@Composable
fun ValueItem(
    name: String,
    value: Any
) = Column(
    modifier = Modifier
        .fillMaxWidth()
        .surface(
            shape = MaterialTheme.shapes.medium,
            backgroundColor = MaterialTheme.colorScheme.surface,
            border = CardDefaults.outlinedCardBorder(false)
        )
        .padding(horizontal = 15.dp, vertical = 10.dp)
) {
    Text(
        text = name,
        style = MaterialTheme.typography.titleMedium
    )

    Text(
        text = value.toString(),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}