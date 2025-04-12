package dev.sanmer.authenticator.ui.screens.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.model.impl.TotpImpl
import dev.sanmer.authenticator.ui.component.SwipeContent
import dev.sanmer.authenticator.ui.ktx.surface
import dev.sanmer.logo.Logo

@Composable
fun AuthItem(
    auth: TotpImpl,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    SwipeContent(
        content = { release ->
            AuthItemButtons(
                onEdit = {
                    release()
                    onEdit()
                },
                onDelete = {
                    release()
                    onDelete()
                }
            )
        },
        surface = {
            AuthItemContent(
                auth = auth,
                enabled = enabled,
                onClick = onClick
            )
        }
    )
}

@Composable
private fun AuthItemContent(
    auth: TotpImpl,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) = Row(
    modifier = Modifier
        .sizeIn(maxWidth = 450.dp)
        .fillMaxWidth()
        .surface(
            shape = MaterialTheme.shapes.large,
            backgroundColor = MaterialTheme.colorScheme.surface,
            border = CardDefaults.outlinedCardBorder()
        )
        .clickable(
            enabled = enabled,
            onClick = onClick
        )
        .padding(all = 15.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp)
) {
    val logo by remember(auth.entity.issuer) {
        derivedStateOf { Logo.getOrDefault(auth.entity.issuer) }
    }

    val otp by auth.otp.collectAsStateWithLifecycle(initialValue = "")

    Image(
        painter = painterResource(id = logo.res),
        contentDescription = null,
        modifier = Modifier.size(40.dp),
        colorFilter = if (logo.refillable) {
            ColorFilter.tint(MaterialTheme.colorScheme.primary)
        } else null
    )

    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = otp.chunked(3).joinToString(" "),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = FontFamily.Monospace
            )
        )

        Text(
            text = auth.entity.displayName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun AuthItemButtons(
    onEdit: () -> Unit,
    onDelete: () -> Unit
) = Row(
    modifier = Modifier.padding(horizontal = 10.dp),
    horizontalArrangement = Arrangement.spacedBy(5.dp)
) {
    FilledTonalIconButton(
        onClick = onEdit
    ) {
        Icon(
            painter = painterResource(id = R.drawable.edit),
            contentDescription = null
        )
    }

    FilledTonalIconButton(
        onClick = onDelete,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {
        Icon(
            painter = painterResource(id = R.drawable.trash_x),
            contentDescription = null
        )
    }
}
