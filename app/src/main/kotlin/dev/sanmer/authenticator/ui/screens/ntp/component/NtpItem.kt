package dev.sanmer.authenticator.ui.screens.ntp.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ui.component.LabelText
import dev.sanmer.authenticator.ui.ktx.surface
import dev.sanmer.authenticator.viewmodel.NtpViewModel
import kotlin.time.Duration
import kotlin.time.DurationUnit

@Composable
fun NtpItem(
    ntp: NtpViewModel.NtpCompat,
    selected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) = Row(
    modifier = Modifier
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
    Image(
        painter = painterResource(id = ntp.brand.res),
        contentDescription = null,
        modifier = Modifier.size(40.dp)
    )

    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = ntp.ntp.name,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = ntp.server.address,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )

        Row(
            modifier = Modifier.padding(top = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            RTTLabel(rtt = ntp.ntpTime.rtt)
            OffsetLabel(offset = ntp.ntpTime.offset)
        }
    }

    if (selected) {
        Spacer(Modifier.weight(1f))

        Icon(
            painter = painterResource(id = R.drawable.circle_check_filled),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(30.dp)
                .align(Alignment.Top)
        )
    }
}

@Composable
private fun OffsetLabel(
    offset: Duration
) {
    val valueStr by remember(offset) {
        derivedStateOf { offset.toString(DurationUnit.MILLISECONDS) }
    }

    LabelText(
        text = stringResource(id = R.string.ntp_offset, valueStr)
    )
}

@Composable
private fun RTTLabel(
    rtt: Duration
) {
    val value by remember(rtt) {
        derivedStateOf { rtt.toLong(DurationUnit.MILLISECONDS) }
    }
    val valueStr by remember(rtt) {
        derivedStateOf { rtt.toString(DurationUnit.MILLISECONDS) }
    }

    LabelText(
        text = if (rtt != Duration.INFINITE) {
            stringResource(id = R.string.ntp_rtt, valueStr)
        } else {
            stringResource(id = R.string.ntp_timeout)
        },
        containerColor = if (isSystemInDarkTheme()) {
            when (value) {
                in 0..<100 -> colorResource(R.color.material_green_900)
                in 100..500 -> colorResource(R.color.material_yellow_900)
                else -> colorResource(R.color.material_red_900)
            }
        } else {
            when (value) {
                in 0..<100 -> colorResource(R.color.material_green_300)
                in 100..500 -> colorResource(R.color.material_yellow_300)
                else -> colorResource(R.color.material_red_300)
            }
        }
    )
}