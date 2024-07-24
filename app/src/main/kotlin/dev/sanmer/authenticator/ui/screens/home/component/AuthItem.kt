package dev.sanmer.authenticator.ui.screens.home.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.model.auth.Auth
import dev.sanmer.authenticator.model.auth.Otp
import dev.sanmer.authenticator.ui.component.PieProgressIndicator
import dev.sanmer.authenticator.ui.component.SwipeContent
import dev.sanmer.authenticator.ui.ktx.surface

@Composable
fun <T> AuthItem(
    auth: T,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) where T : Auth, T : Otp {
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
private fun <T> AuthItemContent(
    auth: T,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) where T : Auth, T : Otp = Row(
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
    horizontalArrangement = Arrangement.spacedBy(10.dp)
) {
    val otp by auth.otp.collectAsStateWithLifecycle(initialValue = auth.now())
    val progress by auth.progress.collectAsStateWithLifecycle(initialValue = 1f)

    Box(
        modifier = Modifier.size(45.dp),
        contentAlignment = Alignment.Center
    ) {
        PieProgressIndicator(
            progress = { progress },
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.fillMaxSize(),
        )

        Logo(
            text = auth.issuer,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        )
    }

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
            text = auth.displayName,
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

@Composable
private fun Logo(
    text: String,
    color: Color = LocalContentColor.current,
    style: TextStyle = LocalTextStyle.current
) {
    if (text.isEmpty()) return

    Text(
        text = text.first().uppercase(),
        color = color,
        style = style
    )
}