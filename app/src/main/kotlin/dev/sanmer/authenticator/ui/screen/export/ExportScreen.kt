package dev.sanmer.authenticator.ui.screen.export

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ui.ktx.isScrollingUp
import dev.sanmer.authenticator.ui.ktx.plus
import dev.sanmer.authenticator.ui.screen.export.component.AuthItem
import dev.sanmer.authenticator.ui.screen.export.component.ButtonsItem
import dev.sanmer.authenticator.ui.screen.export.component.ErrorItem
import dev.sanmer.authenticator.ui.screen.home.component.AuthItem

@Composable
fun ExportScreen(
    viewModel: ExportViewModel,
    goBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()
    val isScrollingUp by listState.isScrollingUp()

    Scaffold(
        topBar = {
            TopBar(
                onBack = goBack,
                onClear = viewModel::clear,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ActionButton(
                onClick = if (viewModel.isEmpty) viewModel::dbImport else viewModel::dbExport,
                isSave = !viewModel.isEmpty,
                visible = isScrollingUp && (viewModel.isEmpty || viewModel.isExternal)
            )
        }
    ) { contentPadding ->
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(15.dp) + contentPadding,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier
                .animateContentSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            item {
                ButtonsItem(
                    password = viewModel.input.password,
                    hidden = viewModel.input.hidden,
                    type = viewModel.input.type,
                    onImport = viewModel::import,
                    onExport = viewModel::export,
                    isEmpty = viewModel.isEmpty
                )
            }

            items(
                items = viewModel.list
            ) { (auth, otp) ->
                otp.onSuccess {
                    AuthItem(
                        auth = auth.auth,
                        otp = it,
                        isSelected = viewModel.isSelected(auth),
                        onClick = { viewModel.pick(auth) }
                    )
                }.onFailure {
                    AuthItem(
                        auth = auth.auth,
                        error = it
                    )
                }
            }

            item {
                viewModel.source.onFailure {
                    ErrorItem(
                        error = it
                    )
                }
            }
        }
    }
}

@Composable
private fun TopBar(
    onBack: () -> Unit,
    onClear: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    title = { Text(text = stringResource(R.string.setting_import_export)) },
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
            onClick = onClear
        ) {
            Icon(
                painter = painterResource(R.drawable.clear_all),
                contentDescription = null
            )
        }
    },
    scrollBehavior = scrollBehavior
)

@Composable
private fun ActionButton(
    onClick: () -> Unit,
    isSave: Boolean,
    visible: Boolean = true
) = AnimatedVisibility(
    visible = visible,
    enter = fadeIn() + scaleIn(),
    exit = scaleOut() + fadeOut()
) {
    FloatingActionButton(
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(
                if (isSave) R.drawable.device_floppy
                else R.drawable.database_import
            ),
            contentDescription = null
        )
    }
}