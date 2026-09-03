package dev.sanmer.authenticator.ui.screen.setting.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.datastore.compose.LocalPreference
import dev.sanmer.authenticator.ui.component.DragHandle
import dev.sanmer.authenticator.ui.ktx.bottom

@Composable
fun PasswordBottomSheet(
    onClose: () -> Unit,
    password: TextFieldState,
    hidden: MutableState<Boolean>,
    onSet: () -> Unit,
    onRemove: () -> Unit,
    onChange: () -> Unit,
) = ModalBottomSheet(
    onDismissRequest = onClose,
    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    shape = MaterialTheme.shapes.large.bottom(0.dp),
    dragHandle = null
) {
    DragHandle()

    Text(
        text = stringResource(R.string.setting_password),
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(15.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        val preference = LocalPreference.current
        val isEmpty by remember(password) {
            derivedStateOf {
                password.text.trim().isEmpty()
            }
        }

        OutlinedSecureTextField(
            state = password,
            trailingIcon = {
                IconButton(
                    onClick = { hidden.value = !hidden.value }
                ) {
                    Icon(
                        painter = painterResource(
                            if (hidden.value) R.drawable.eye_closed
                            else R.drawable.eye
                        ),
                        contentDescription = null
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            shape = MaterialTheme.shapes.medium,
            textObfuscationMode = with(TextObfuscationMode) { if (hidden.value) Hidden else Visible },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            if (preference.isEncrypted) {
                OutlinedButton(
                    onClick = onRemove,
                    enabled = isEmpty,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.setting_password_remove))
                }

                Button(
                    onClick = onChange,
                    enabled = !isEmpty,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.setting_password_change))
                }
            } else {
                Button(
                    onClick = onSet,
                    enabled = !isEmpty,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.token_save))
                }
            }
        }
    }
}