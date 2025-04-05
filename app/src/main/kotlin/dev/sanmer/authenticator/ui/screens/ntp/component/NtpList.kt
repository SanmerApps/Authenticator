package dev.sanmer.authenticator.ui.screens.ntp.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.datastore.model.Ntp
import dev.sanmer.authenticator.ui.ktx.plus
import dev.sanmer.authenticator.ui.provider.LocalPreference
import dev.sanmer.authenticator.viewmodel.NtpViewModel

@Composable
fun NtpList(
    state: LazyListState,
    ntps: List<NtpViewModel.NtpCompat>,
    setNtp: (Ntp) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val preference = LocalPreference.current

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        state = state,
        contentPadding = contentPadding + PaddingValues(15.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(ntps) {
            NtpItem(
                ntp = it,
                selected = preference.ntp == it.ntp,
                onClick = { setNtp(it.ntp) }
            )
        }
    }
}