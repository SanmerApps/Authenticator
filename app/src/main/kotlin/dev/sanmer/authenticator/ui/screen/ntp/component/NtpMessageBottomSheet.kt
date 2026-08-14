package dev.sanmer.authenticator.ui.screen.ntp.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sanmer.auth.ntp.NtpMessage
import dev.sanmer.authenticator.Const.DATETIME_DISPLAY
import dev.sanmer.authenticator.datastore.model.Ntp
import dev.sanmer.authenticator.ui.component.DragHandle
import dev.sanmer.authenticator.ui.ktx.bottom
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime

@Composable
fun NtpMessageBottomSheet(
    onClose: () -> Unit,
    ntp: Ntp,
    message: NtpMessage
) = ModalBottomSheet(
    onDismissRequest = onClose,
    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    shape = MaterialTheme.shapes.large.bottom(0.dp),
    dragHandle = null
) {
    DragHandle()

    Text(
        text = ntp.name(),
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(15.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        ValueItem(
            name = "Version",
            value = message.version
        )

        ValueItem(
            name = "Stratum",
            value = message.stratum
        )

        ValueItem(
            name = "Precision",
            value = message.precision
        )

        ValueItem(
            name = "Root Delay",
            value = message.rootDelay
        )

        ValueItem(
            name = "Root Dispersion",
            value = message.rootDispersion
        )

        if (message.stratum == NtpMessage.Stratum.Primary) {
            ValueItem(
                name = "Reference ID",
                value = message.referenceIdentifier.kissCode()
            )
        }

        val referenceTimestamp by remember {
            derivedStateOf {
                message.referenceTimestamp
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .format(DATETIME_DISPLAY)
            }
        }

        ValueItem(
            name = "Reference Timestamp",
            value = referenceTimestamp
        )
    }
}