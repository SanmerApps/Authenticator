package dev.sanmer.authenticator.ui.screens.edit.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ui.component.DragHandle
import dev.sanmer.authenticator.ui.ktx.bottom
import dev.sanmer.qrcode.QRCode

@Composable
fun SecretItem(
    secret: String,
    uriString: String,
    onSecretChange: (String) -> Unit,
    isError: Boolean,
    readOnly: Boolean
) {
    var show by rememberSaveable { mutableStateOf(false) }
    if (show) QRCodeBottomSheet(
        uri = uriString,
        onDismiss = { show = false }
    )

    TextFieldContent(
        leading = { TextFieldContentIcon(icon = R.drawable.key) },
        trailing = {
            if (readOnly && uriString.isNotEmpty()) {
                IconButton(
                    onClick = { show = true }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.qrcode),
                        contentDescription = null
                    )
                }
            } else {
                TextFieldContentSpacer()
            }
        }
    ) {
        val passwordVisualTransformation = remember { PasswordVisualTransformation() }

        TextField(
            value = secret,
            onValueChange = onSecretChange,
            label = stringResource(id = R.string.edit_secret),
            modifier = Modifier.weight(1f),
            readOnly = readOnly,
            isError = isError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            visualTransformation = if (readOnly) {
                passwordVisualTransformation
            } else {
                VisualTransformation.None
            },
        )
    }
}

@Composable
private fun QRCodeBottomSheet(
    uri: String,
    onDismiss: () -> Unit,
) = ModalBottomSheet(
    onDismissRequest = onDismiss,
    shape = MaterialTheme.shapes.large.bottom(0.dp),
    dragHandle = null
) {
    DragHandle()

    Text(
        text = stringResource(id = R.string.edit_qrcode),
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )

    Box(
        modifier = Modifier
            .padding(vertical = 15.dp, horizontal = 40.dp)
            .align(Alignment.CenterHorizontally),
        contentAlignment = Alignment.Center
    ) {
        val sizePx = with(LocalDensity.current) {
            BottomSheetDefaults.SheetMaxWidth.roundToPx()
        }

        val foregroundColor = MaterialTheme.colorScheme.onSurface.toArgb()
        val backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow.toArgb()

        val bitmap by remember {
            derivedStateOf {
                QRCode.encodeToBitmap(
                    contents = uri,
                    size = sizePx,
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
}