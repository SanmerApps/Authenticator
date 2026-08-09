package dev.sanmer.authenticator.ui.screen.ntp.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.auth.ntp.NtpClock
import dev.sanmer.auth.ntp.NtpServer
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.datastore.model.Ntp
import dev.sanmer.authenticator.model.LoadData
import dev.sanmer.authenticator.ui.component.LabelText
import dev.sanmer.authenticator.ui.ktx.surface
import java.net.SocketTimeoutException
import kotlin.time.Duration
import kotlin.time.DurationUnit

private fun Ntp.brand() = when (this) {
    Ntp.Custom -> R.drawable.clock_record_thin
    Ntp.Alibaba -> dev.sanmer.brand.R.drawable.brand_aliyun
    Ntp.Apple -> dev.sanmer.brand.R.drawable.brand_apple
    Ntp.Amazon -> dev.sanmer.brand.R.drawable.brand_aws
    Ntp.Cloudflare -> dev.sanmer.brand.R.drawable.brand_cloudflare
    Ntp.Google -> dev.sanmer.brand.R.drawable.brand_googlecloud
    Ntp.Meta -> dev.sanmer.brand.R.drawable.brand_meta
    Ntp.Microsoft -> dev.sanmer.brand.R.drawable.brand_azure
    Ntp.Tencent -> dev.sanmer.brand.R.drawable.brand_tencentcloud
}

@Composable
fun Ntp.name() = when (this) {
    Ntp.Custom -> stringResource(R.string.ntp_custom)
    else -> name
}

@Composable
fun NtpItem(
    ntp: Ntp,
    server: NtpServer,
    clock: LoadData<NtpClock>,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) = Row(
    modifier = modifier
        .fillMaxWidth()
        .surface(
            shape = MaterialTheme.shapes.large,
            backgroundColor = MaterialTheme.colorScheme.surface,
            border = CardDefaults.outlinedCardBorder(false)
        )
        .combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick
        )
        .padding(15.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(13.dp)
) {
    Image(
        painter = painterResource(ntp.brand()),
        contentDescription = null,
        modifier = Modifier.size(45.dp),
        colorFilter = if (ntp != Ntp.Custom) null else
            ColorFilter.tint(MaterialTheme.colorScheme.primary)
    )

    Column(
        modifier = Modifier.weight(1f)
    ) {
        Text(
            text = ntp.name(),
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = server.address,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )

        if (!clock.isPending) AnimatedContent(
            modifier = Modifier.padding(top = 5.dp),
            targetState = clock,
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { clock ->
            clock.onLoading {
                LinearProgressIndicator(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .size(width = 120.dp, height = (5.5).dp)
                )
            }.onSuccess {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    RTTLabel(it.rtt)
                    OffsetLabel(it.offset)
                }
            }.onFailure {
                ErrorLabel(it)
            }
        }
    }

    if (isSelected) Icon(
        painter = painterResource(R.drawable.circle_check_filled),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .size(30.dp)
            .align(Alignment.Top)
    )
}

@Composable
private fun RTTLabel(
    rtt: Duration
) {
    val value by remember(rtt) {
        derivedStateOf { rtt.toLong(DurationUnit.MILLISECONDS) }
    }
    val display by remember(rtt) {
        derivedStateOf { rtt.toString(DurationUnit.MILLISECONDS) }
    }

    LabelText(
        text = display,
        containerColor = if (isSystemInDarkTheme()) {
            when (value) {
                in 0..<100 -> Color(0xFF1B5E20)
                in 100..500 -> Color(0xFFF57F17)
                else -> Color(0xFFB71C1C)
            }
        } else {
            when (value) {
                in 0..<100 -> Color(0xFF81C784)
                in 100..500 -> Color(0xFFFFF176)
                else -> Color(0xFFE57373)
            }
        }
    )
}

@Composable
private fun OffsetLabel(
    offset: Duration
) {
    val display by remember(offset) {
        derivedStateOf {
            offset.toString(DurationUnit.MILLISECONDS)
                .let {
                    if (offset.isPositive()) "+$it" else it
                }
        }
    }

    LabelText(text = display)
}

@Composable
private fun ErrorLabel(
    error: Throwable
) = LabelText(
    text = if (error is SocketTimeoutException) {
        stringResource(R.string.ntp_timeout)
    } else {
        error.message ?: error.javaClass.name
    },
    containerColor = if (isSystemInDarkTheme()) {
        Color(0xFFB71C1C)
    } else {
        Color(0xFFE57373)
    }
)