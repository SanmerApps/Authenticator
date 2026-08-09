package dev.sanmer.authenticator.ui.screen.ntp.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sanmer.auth.ntp.NtpClock
import dev.sanmer.auth.ntp.NtpMessage
import dev.sanmer.auth.ntp.NtpServer
import dev.sanmer.authenticator.datastore.model.Ntp
import dev.sanmer.authenticator.model.LoadData
import dev.sanmer.authenticator.ui.ktx.plus

@Composable
fun NtpList(
    list: List<Pair<Ntp, NtpServer>>,
    clock: (NtpServer) -> LoadData<NtpClock>,
    isSelected: (Ntp) -> Boolean,
    onPick: (Ntp, NtpServer) -> Unit,
    onView: (Ntp, NtpMessage) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp)
) = LazyColumn(
    modifier = modifier,
    state = state,
    contentPadding = PaddingValues(15.dp) + contentPadding,
    verticalArrangement = Arrangement.spacedBy(15.dp)
) {
    items(
        items = list,
        key = { (ntp, _) -> ntp }
    ) { (ntp, server) ->
        NtpItem(
            ntp = ntp,
            server = server,
            clock = clock(server),
            isSelected = isSelected(ntp),
            onClick = { onPick(ntp, server) },
            onLongClick = {
                clock(server).onSuccess {
                    onView(ntp, it.message)
                }
            }
        )
    }
}