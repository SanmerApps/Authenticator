package dev.sanmer.authenticator.ui.screen.home.component

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
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.database.model.Auth
import dev.sanmer.authenticator.ui.component.LabelText
import dev.sanmer.authenticator.ui.ktx.setSensitiveText
import dev.sanmer.authenticator.ui.ktx.surface
import dev.sanmer.brand.Brand
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun AuthItem(
    auth: Auth,
    otp: StateFlow<String>,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val otp by otp.collectAsStateWithLifecycle()
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .surface(
                shape = MaterialTheme.shapes.large,
                backgroundColor = MaterialTheme.colorScheme.surface,
                border = CardDefaults.outlinedCardBorder(false)
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = {
                    scope.launch { clipboard.setSensitiveText(otp) }
                }
            )
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        val brand by remember(auth.id) {
            derivedStateOf { Brand.valueOfOrNull(auth.issuer) }
        }

        Image(
            painter = painterResource(brand?.id ?: R.drawable.fingerprint_thin),
            contentDescription = null,
            modifier = Modifier.size(45.dp),
            colorFilter = if (brand != null) null else
                ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OtpItem(
                    otp = otp
                )

                if (brand == null && auth.issuer.isNotEmpty()) LabelText(
                    text = auth.issuer
                )
            }

            if (auth.name.isNotEmpty()) Text(
                text = auth.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }

        if (isSelected) Icon(
            painter = painterResource(R.drawable.circle_check_filled),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
private fun OtpItem(
    otp: String,
    modifier: Modifier = Modifier
) = Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically
) {
    otp.forEachIndexed { index, char ->
        AnimatedDigit(
            digit = char,
            position = index
        )
        if ((index + 1) % 3 == 0 && index < otp.lastIndex) {
            Spacer(modifier = Modifier.width(12.dp))
        }
    }
}

@Composable
private fun AnimatedDigit(
    digit: Char,
    position: Int,
    isOddPosition: Boolean = position % 2 == 0,
    enterDirection: Int = if (isOddPosition) -1 else 1,
    exitDirection: Int = if (isOddPosition) 1 else -1
) = AnimatedContent(
    targetState = digit,
    transitionSpec = {
        slideIn(
            animationSpec = tween(500)
        ) {
            IntOffset(0, enterDirection * it.height)
        } + scaleIn(
            animationSpec = tween(500)
        ) + fadeIn(
            animationSpec = tween(500)
        ) togetherWith slideOut(
            animationSpec = tween(500)
        ) {
            IntOffset(0, exitDirection * it.height)
        } + scaleOut(
            animationSpec = tween(500)
        ) + fadeOut(
            animationSpec = tween(500)
        )
    }
) { digit ->
    Text(
        text = digit.toString(),
        style = MaterialTheme.typography.headlineSmall,
        fontFamily = FontFamily.Monospace
    )
}