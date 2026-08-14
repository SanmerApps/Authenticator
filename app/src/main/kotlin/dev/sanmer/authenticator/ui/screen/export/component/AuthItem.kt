package dev.sanmer.authenticator.ui.screen.export.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.database.model.Auth
import dev.sanmer.authenticator.ui.component.LabelText
import dev.sanmer.authenticator.ui.ktx.surface

@Composable
fun AuthItem(
    auth: Auth,
    error: Throwable,
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier
        .fillMaxWidth()
        .surface(
            shape = MaterialTheme.shapes.large,
            backgroundColor = MaterialTheme.colorScheme.surface,
            border = CardDefaults.outlinedCardBorder(false)
        )
        .padding(15.dp)
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (auth.name.isNotEmpty()) Text(
            text = auth.name,
            style = MaterialTheme.typography.titleMedium
        )

        if (auth.issuer.isNotEmpty()) LabelText(
            text = auth.issuer
        )
    }

    Text(
        text = error.message ?: error.javaClass.name,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.error
    )
}