package dev.sanmer.authenticator.ui.screen.export.component

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
fun ErrorItem(
    error: Throwable,
    modifier: Modifier = Modifier
) = Text(
    text = error.message ?: error.javaClass.name,
    style = MaterialTheme.typography.bodyLarge,
    color = MaterialTheme.colorScheme.error,
    modifier = modifier
        .fillMaxWidth()
        .surface(
            shape = MaterialTheme.shapes.large,
            backgroundColor = MaterialTheme.colorScheme.surface,
            border = CardDefaults.outlinedCardBorder(false)
        )
        .padding(15.dp)
)