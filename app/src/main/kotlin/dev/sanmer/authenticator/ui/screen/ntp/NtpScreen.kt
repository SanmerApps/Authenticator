package dev.sanmer.authenticator.ui.screen.ntp

import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.datastore.compose.LocalPreference
import dev.sanmer.authenticator.ui.screen.ntp.NtpViewModel.BottomSheet
import dev.sanmer.authenticator.ui.screen.ntp.component.CustomBottomSheet
import dev.sanmer.authenticator.ui.screen.ntp.component.NtpList
import dev.sanmer.authenticator.ui.screen.ntp.component.NtpMessageBottomSheet

@Composable
fun NtpScreen(
    viewModel: NtpViewModel,
    goBack: () -> Unit
) {
    val preference = LocalPreference.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    when (val bs = viewModel.bottomSheet) {
        BottomSheet.None -> {}
        BottomSheet.Custom -> CustomBottomSheet(
            onClose = { viewModel.bottomSheet = BottomSheet.None },
            ntpAddress = viewModel.ntpAddress,
            onSave = viewModel::setNtpAddress
        )

        is BottomSheet.NtpMsg -> NtpMessageBottomSheet(
            onClose = { viewModel.bottomSheet = BottomSheet.None },
            ntp = bs.ntp,
            message = bs.msg
        )
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopBar(
                onBack = goBack,
                onCustom = { viewModel.bottomSheet = BottomSheet.Custom },
                scrollBehavior = scrollBehavior
            )
        },
    ) { contentPadding ->
        NtpList(
            list = viewModel.list,
            clock = { viewModel.clock(it.address) },
            isSelected = { it == preference.ntp },
            onPick = viewModel::pick,
            onView = { ntp, msg -> viewModel.bottomSheet = BottomSheet.NtpMsg(ntp, msg) },
            contentPadding = contentPadding,
            state = viewModel.listState,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        )
    }
}

@Composable
private fun TopBar(
    onBack: () -> Unit,
    onCustom: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    title = { Text(text = stringResource(R.string.ntp_title)) },
    navigationIcon = {
        IconButton(
            onClick = onBack
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_left),
                contentDescription = null
            )
        }
    },
    actions = {
        IconButton(
            onClick = onCustom
        ) {
            Icon(
                painter = painterResource(R.drawable.plus),
                contentDescription = null
            )
        }
    },
    scrollBehavior = scrollBehavior
)