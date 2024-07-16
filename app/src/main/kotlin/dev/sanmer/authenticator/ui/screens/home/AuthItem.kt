package dev.sanmer.authenticator.ui.screens.home

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sanmer.authenticator.model.auth.Auth
import dev.sanmer.authenticator.model.auth.Otp
import dev.sanmer.authenticator.ui.component.PieProgressIndicator

@Composable
fun <T> OtpItem(
    auth: T,
    shape: Shape = RoundedCornerShape(15.dp),
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) where T : Auth, T : Otp = Row(
    modifier = Modifier
        .clip(shape)
        .border(
            border = CardDefaults.outlinedCardBorder(),
            shape = shape
        )
        .clickable(
            enabled = enabled,
            onClick = onClick
        )
        .padding(all = 15.dp)
        .fillMaxWidth(),
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
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = auth.displayName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
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