package dev.sanmer.authenticator.ui.screens.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ui.ktx.setSensitiveText
import dev.sanmer.authenticator.ui.screens.edit.component.DigitsAndPeriodItem
import dev.sanmer.authenticator.ui.screens.edit.component.SecretItem
import dev.sanmer.authenticator.ui.screens.edit.component.TextFieldItem
import dev.sanmer.authenticator.ui.screens.edit.component.TypeAndHashItem
import dev.sanmer.authenticator.viewmodel.EditViewModel
import dev.sanmer.authenticator.viewmodel.EditViewModel.Value
import kotlinx.coroutines.launch

@Composable
fun EditScreen(
    viewModel: EditViewModel = hiltViewModel(),
    navController: NavController
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopBar(
                isEdit = viewModel.isEdit,
                uri = viewModel.uriString,
                onSave = {
                    viewModel.save { if (!viewModel.isEdit) navController.navigateUp() }
                },
                navController = navController,
                scrollBehavior = scrollBehavior
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .imePadding()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
                .padding(vertical = 15.dp, horizontal = 5.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextFieldItem(
                value = viewModel.input.name,
                onValueChange = { name ->
                    viewModel.update { it.copy(name = name) }
                },
                leadingIcon = R.drawable.user,
                label = stringResource(id = R.string.edit_name),
                isError = viewModel.isError(Value.Name)
            )

            TextFieldItem(
                value = viewModel.input.issuer,
                onValueChange = { issuer ->
                    viewModel.update { it.copy(issuer = issuer) }
                },
                label = stringResource(id = R.string.edit_issuer),
                isError = viewModel.isError(Value.Issuer)
            )

            SecretItem(
                secret = viewModel.input.secret,
                uriString = viewModel.uriString,
                onSecretChange = { secret ->
                    viewModel.update { it.copy(secret = secret) }
                },
                isError = viewModel.isError(Value.Secret),
                readOnly = viewModel.isEdit
            )

            TypeAndHashItem(
                type = viewModel.input.type,
                onTypeChange = { type ->
                    viewModel.update { it.copy(type = type) }
                },
                hash = viewModel.input.hash,
                onHashChange = { hash ->
                    viewModel.update { it.copy(hash = hash) }
                },
                readOnly = viewModel.isEdit
            )

            DigitsAndPeriodItem(
                type = viewModel.input.type,
                digits = viewModel.input.digits,
                onDigitsChange = { digits ->
                    viewModel.update { it.copy(digits = digits) }
                },
                period = viewModel.input.period,
                onPeriodChange = { period ->
                    viewModel.update { it.copy(period = period) }
                },
                readOnly = viewModel.isEdit
            )
        }
    }
}

@Composable
private fun TopBar(
    isEdit: Boolean,
    uri: String,
    onSave: () -> Unit,
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    title = {
        Text(
            text = stringResource(
                id = if (isEdit) R.string.edit_edit_title else R.string.edit_add_title
            )
        )
    },
    navigationIcon = {
        IconButton(
            onClick = { navController.navigateUp() }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.x),
                contentDescription = null
            )
        }
    },
    actions = {
        val clipboard = LocalClipboard.current
        val scope = rememberCoroutineScope()

        if (uri.isNotBlank()) {
            IconButton(
                onClick = {
                    scope.launch {
                        clipboard.setSensitiveText(uri)
                    }
                },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.clipboard_text),
                    contentDescription = null
                )
            }
        }

        IconButton(
            onClick = onSave
        ) {
            Icon(
                painter = painterResource(id = R.drawable.device_floppy),
                contentDescription = null
            )
        }
    },
    scrollBehavior = scrollBehavior
)