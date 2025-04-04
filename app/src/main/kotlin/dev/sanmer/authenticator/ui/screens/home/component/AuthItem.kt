package dev.sanmer.authenticator.ui.screens.home.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.model.auth.Auth
import dev.sanmer.authenticator.model.auth.Otp
import dev.sanmer.authenticator.ui.component.SwipeContent
import dev.sanmer.authenticator.ui.ktx.surface
import dev.sanmer.authenticator.ui.theme.JetBrainsMono

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
            shape = MaterialTheme.shapes.medium,
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
        modifier = Modifier.size(40.dp),
        contentAlignment = Alignment.Center
    ) {
        val num by remember { derivedStateOf { otp.first().toString().toInt() } }
        CircularProgressIndicator(
            progress = { progress },
            color = when {
                isSystemInDarkTheme() -> colorDark(num = num)
                else -> colorLight(num = num)
            },
            modifier = Modifier.fillMaxSize(),
        )

        Logo(
            text = auth.issuer,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = JetBrainsMono,
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
        text = text[0].uppercase(),
        color = color,
        style = style
    )
}

@Composable
private fun colorLight(num: Int): Color {
    return when (num) {
        0 -> colorResource(id = R.color.material_red_300)
        1 -> colorResource(id = R.color.material_orange_300)
        2 -> colorResource(id = R.color.material_yellow_300)
        3 -> colorResource(id = R.color.material_green_300)
        4 -> colorResource(id = R.color.material_teal_300)
        5 -> colorResource(id = R.color.material_blue_300)
        6 -> colorResource(id = R.color.material_indigo_300)
        7 -> colorResource(id = R.color.material_purple_300)
        8 -> colorResource(id = R.color.material_pink_300)
        9 -> colorResource(id = R.color.material_deep_orange_300)
        else -> MaterialTheme.colorScheme.primary
    }
}

@Composable
private fun colorDark(num: Int): Color {
    return when (num) {
        0 -> colorResource(id = R.color.material_red_900)
        1 -> colorResource(id = R.color.material_orange_900)
        2 -> colorResource(id = R.color.material_yellow_900)
        3 -> colorResource(id = R.color.material_green_900)
        4 -> colorResource(id = R.color.material_teal_900)
        5 -> colorResource(id = R.color.material_blue_900)
        6 -> colorResource(id = R.color.material_indigo_900)
        7 -> colorResource(id = R.color.material_purple_900)
        8 -> colorResource(id = R.color.material_pink_900)
        9 -> colorResource(id = R.color.material_deep_orange_900)
        else -> MaterialTheme.colorScheme.primary
    }
}