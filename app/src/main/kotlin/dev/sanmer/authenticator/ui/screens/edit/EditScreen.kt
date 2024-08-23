package dev.sanmer.authenticator.ui.screens.edit

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import dev.sanmer.authenticator.ui.ktx.setSensitiveText
import dev.sanmer.authenticator.ui.ktx.surface
import dev.sanmer.authenticator.ui.screens.edit.component.DigitsItem
import dev.sanmer.authenticator.ui.screens.edit.component.TextFieldItem
import dev.sanmer.authenticator.ui.screens.edit.component.TypeItem
import dev.sanmer.authenticator.viewmodel.EditViewModel
import dev.sanmer.authenticator.viewmodel.EditViewModel.Value
import dev.sanmer.qrcode.QRCode

@Composable
fun EditScreen(
    viewModel: EditViewModel = hiltViewModel(),
    navController: NavController
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopBar(
                edit = viewModel.edit,
                uri = viewModel.uriString,
                fromUri = viewModel::updateFromUri,
                onSave = {
                    viewModel.save { if (!viewModel.edit) navController.navigateUp() }
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

            TextFieldItem(
                value = viewModel.input.secret,
                onValueChange = { secret ->
                    viewModel.update { it.copy(secret = secret) }
                },
                leadingIcon = R.drawable.key,
                label = stringResource(id = R.string.edit_secret),
                hidden = viewModel.edit,
                isError = viewModel.isError(Value.Secret),
                trailingIcon = {
                    if (viewModel.edit) {
                        ToggleQRCode(
                            show = viewModel.showQr,
                            onClick = { viewModel.updateShowQr { !it } }
                        )
                    } else {
                        IconButton(
                            onClick = viewModel::randomSecret,
                            enabled = !viewModel.isOtpUri
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.arrows_shuffle),
                                contentDescription = null
                            )
                        }
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
                readOnly = viewModel.edit
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
                readOnly = viewModel.edit
            )

            if (viewModel.showQr && viewModel.uriString.isNotEmpty()) {
                QRCodeItem(
                    uri = viewModel.uriString
                )
            }
        }
    }
}

@Composable
private fun QRCodeItem(
    uri: String,
    modifier: Modifier = Modifier,
    size: Dp = 240.dp,
    shape: Shape = MaterialTheme.shapes.small
) = Box(
    modifier = modifier
        .size(size = size)
        .surface(
            shape = shape,
            backgroundColor = MaterialTheme.colorScheme.surface,
            border = CardDefaults.outlinedCardBorder()
        ),
    contentAlignment = Alignment.Center
) {
    val sizePx = with(LocalDensity.current) {
        (size - 5.dp).roundToPx()
    }

    val foregroundColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val backgroundColor = MaterialTheme.colorScheme.surface.toArgb()

    val bitmap by remember {
        derivedStateOf {
            QRCode.encodeToBitmap(
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
private fun ToggleQRCode(
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
    edit: Boolean,
    uri: String,
    fromUri: (String) -> Unit,
    onSave: () -> Unit,
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    title = {
        Text(
            text = stringResource(
                id = if (edit) R.string.edit_edit_tile else R.string.edit_add_tile
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
        val clipboardManager = LocalClipboardManager.current

        IconButton(
            onClick = {
                if (edit) {
                    clipboardManager.setSensitiveText(uri)
                } else {
                    clipboardManager.getText()?.let { fromUri(it.text) }
                }
            },
            enabled = uri.isNotBlank() || clipboardManager.hasText()
        ) {
            Icon(
                painter = painterResource(
                    id = if (edit) {
                        R.drawable.clipboard_copy
                    } else {
                        R.drawable.clipboard_text
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
    scrollBehavior = scrollBehavior
)