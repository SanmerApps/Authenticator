package dev.sanmer.authenticator.ui.screens.authorize.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ui.AuthorizeActivity.Action
import dev.sanmer.authenticator.ui.screens.settings.component.SettingBottomSheet

@Composable
fun EnterPasswordItem(
    onDismiss: () -> Unit,
    action: Action,
    isPasswordError: Boolean,
    onEnter: (String) -> Unit,
    enableBiometric: Boolean = false,
    onBiometric: () -> Unit = {}
) {
    var currentPassword by remember { mutableStateOf("") }

    SettingBottomSheet(
        onDismiss = onDismiss,
        title = stringResource(
            id = when (action) {
                Action.SetupPassword -> R.string.security_set_password
                Action.RemovePassword -> R.string.security_remove_password
                else -> R.string.security_enter_password
            }
        ),
    ) {
        PasswordTextField(
            password = currentPassword,
            onPasswordChange = { currentPassword = it },
            onImeDone = { onEnter(currentPassword) },
            isError = isPasswordError,
            title = stringResource(
                id = when (action) {
                    Action.SetupPassword -> R.string.security_new_password
                    else -> R.string.security_current_password
                }
            ),
            modifier = Modifier.fillMaxWidth(),
            actionIcon = if (enableBiometric) R.drawable.fingerprint else null,
            onActionClick = if (enableBiometric) onBiometric else null
        )
    }
}