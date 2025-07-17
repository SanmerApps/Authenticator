package dev.sanmer.authenticator.ui.screens.home.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.IntOffset
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

    val otp by auth.otp.collectAsStateWithLifecycle(initialValue = auth.now())

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
        OtpItem(
            otp = otp
        )

        Text(
            text = auth.entity.displayName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun OtpItem(
    otp: String
) = Row(
    verticalAlignment = Alignment.CenterVertically
) {
    otp.forEachIndexed { index, char ->
        AnimatedDigit(
            digit = char,
            position = index
        )
        if ((index + 1) % 3 == 0 && index < otp.length - 1) {
            Spacer(modifier = Modifier.width(12.dp))
        }
    }
}

@Composable
private fun AnimatedDigit(
    digit: Char,
    position: Int
) {
    val isOddPosition = remember { (position % 2 == 0) }
    val enterDirection = remember { if (isOddPosition) -1 else 1 }
    val exitDirection = remember { if (isOddPosition) 1 else -1 }

    AnimatedContent(
        targetState = digit,
        transitionSpec = {
            slideIn(
                animationSpec = tween(500)
            ) {
                IntOffset(0, enterDirection * it.height)
            } + scaleIn(
                animationSpec = tween(durationMillis = 500)
            ) + fadeIn(
                animationSpec = tween(durationMillis = 500)
            ) togetherWith slideOut(
                animationSpec = tween(500)
            ) {
                IntOffset(0, exitDirection * it.height)
            } + scaleOut(
                animationSpec = tween(durationMillis = 500)
            ) + fadeOut(
                animationSpec = tween(durationMillis = 500)
            )
        }
    ) { targetDigit ->
        Text(
            text = targetDigit.toString(),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = FontFamily.Monospace
            )
        )
    }
}
