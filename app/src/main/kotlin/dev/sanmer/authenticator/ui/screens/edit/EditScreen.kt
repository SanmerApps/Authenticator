package dev.sanmer.authenticator.ui.screens.edit

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ui.component.NavigateUpTopBar
import dev.sanmer.authenticator.ui.ktx.setSensitiveText
import dev.sanmer.authenticator.ui.ktx.surface
import dev.sanmer.authenticator.ui.screens.edit.component.DigitsItem
import dev.sanmer.authenticator.ui.screens.edit.component.TextFieldItem
import dev.sanmer.authenticator.ui.screens.edit.component.TypeItem
import dev.sanmer.authenticator.viewmodel.EditViewModel
import dev.sanmer.qrcode.QrCodeCompat

@Composable
fun EditScreen(
    viewModel: EditViewModel = hiltViewModel(),
    navController: NavController
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopBar(
                addAccount = viewModel.addAccount,
                uri = viewModel.uriString,
                fromUri = viewModel::decodeFromUri,
                onSave = {
                    viewModel.save {
                        if (viewModel.addAccount) navController.popBackStack()
                    }
                },
                navController = navController,
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .imePadding()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 15.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextFieldItem(
                value = viewModel.input.name,
                onValueChange = { name ->
                    viewModel.update { it.copy(name = name) }
                },
                icon = R.drawable.user,
                label = stringResource(id = R.string.edit_name),
                isError = viewModel.isFailed(EditViewModel.Check.Name)
            )

            TextFieldItem(
                value = viewModel.input.issuer,
                onValueChange = { issuer ->
                    viewModel.update { it.copy(issuer = issuer) }
                },
                label = stringResource(id = R.string.edit_issuer),
                isError = viewModel.isFailed(EditViewModel.Check.Issuer)
            )

            TextFieldItem(
                value = viewModel.input.secret,
                onValueChange = { secret ->
                    viewModel.update { it.copy(secret = secret) }
                },
                icon = R.drawable.key,
                label = stringResource(id = R.string.edit_secret),
                hidden = !viewModel.addAccount,
                isError = viewModel.isFailed(EditViewModel.Check.Secret),
                trailingIcon = if (viewModel.addAccount) null else {
                    {
                        ToggleQrCode(
                            show = viewModel.showQrCode,
                            onClick = { viewModel.updateShowQrCode { !it } }
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )

            TypeItem(
                type = viewModel.input.type,
                onTypeChange = { type ->
                    viewModel.update { it.copy(type = type) }
                },
                hash = viewModel.input.hash,
                onHashChange = { hash ->
                    viewModel.update { it.copy(hash = hash) }
                },
                readOnly = !viewModel.addAccount
            )

            DigitsItem(
                type = viewModel.input.type,
                digits = viewModel.input.digits,
                onDigitsChange = { digits ->
                    viewModel.update { it.copy(digits = digits) }
                },
                counter = viewModel.input.counter,
                onCounterChange = { counter ->
                    viewModel.update { it.copy(counter = counter) }
                },
                period = viewModel.input.period,
                onPeriodChange = { period ->
                    viewModel.update { it.copy(period = period) }
                },
                readOnly = !viewModel.addAccount
            )

            if (viewModel.showQrCode && viewModel.uriString.isNotEmpty()) {
                QrCodeItem(
                    uri = viewModel.uriString
                )
            }
        }
    }
}

@Composable
private fun QrCodeItem(
    uri: String,
    modifier: Modifier = Modifier,
    size: Dp = 230.dp,
    shape: Shape = MaterialTheme.shapes.small
) = Box(
    modifier = modifier
        .size(size = 240.dp)
        .surface(
            shape = shape,
            backgroundColor = MaterialTheme.colorScheme.surface,
            border = CardDefaults.outlinedCardBorder()
        ),
    contentAlignment = Alignment.Center
) {
    val sizePx = with(LocalDensity.current) {
        size.roundToPx()
    }

    val foregroundColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val backgroundColor = MaterialTheme.colorScheme.surface.toArgb()

    val bitmap by remember {
        derivedStateOf {
            QrCodeCompat.encodeToBitmap(
                contents = uri,
                width = sizePx,
                height = sizePx,
                foregroundColor = foregroundColor,
                backgroundColor = backgroundColor
            )
        }
    }

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = null
    )
}

@Composable
private fun ToggleQrCode(
    show: Boolean,
    onClick: () -> Unit
) = IconButton(
    onClick = onClick
) {
    Icon(
        painter = painterResource(
            id = if (show) {
                R.drawable.qrcode
            } else {
                R.drawable.qrcode_off
            }
        ),
        contentDescription = null
    )
}

@Composable
private fun TopBar(
    addAccount: Boolean,
    uri: String,
    fromUri: (String) -> Unit,
    onSave: () -> Unit,
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior
) = NavigateUpTopBar(
    title = stringResource(
        id = if (addAccount) {
            R.string.edit_add_tile
        } else {
            R.string.edit_edit_tile
        }
    ),
    actions = {
        val clipboardManager = LocalClipboardManager.current

        IconButton(
            onClick = {
                if (addAccount) {
                    clipboardManager.getText()?.let { fromUri(it.text) }
                } else {
                    clipboardManager.setSensitiveText(uri)
                }
            },
            enabled = uri.isNotBlank() || clipboardManager.hasText()
        ) {
            Icon(
                painter = painterResource(
                    id = if (addAccount) {
                        R.drawable.clipboard_list
                    } else {
                        R.drawable.clipboard_copy
                    }
                ),
                contentDescription = null
            )
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
    navController = navController,
    scrollBehavior = scrollBehavior
)