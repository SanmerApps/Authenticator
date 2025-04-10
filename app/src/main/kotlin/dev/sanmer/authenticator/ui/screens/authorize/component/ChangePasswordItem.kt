package dev.sanmer.authenticator.ui.screens.authorize.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ui.screens.settings.component.SettingBottomSheet

@Composable
fun ChangePasswordItem(
    onDismiss: () -> Unit,
    isPasswordError: Boolean,
    onChange: (String, String) -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    SettingBottomSheet(
        onDismiss = onDismiss,
        title = stringResource(id = R.string.security_change_password),
    ) {
        PasswordTextField(
            password = currentPassword,
            onPasswordChange = { currentPassword = it },
            imeAction = ImeAction.Next,
            isError = isPasswordError,
            title = stringResource(id = R.string.security_current_password),
            modifier = Modifier.fillMaxWidth()
        )

        PasswordTextField(
            password = newPassword,
            onPasswordChange = { newPassword = it },
            onImeDone = { onChange(currentPassword, newPassword) },
            title = stringResource(id = R.string.security_new_password),
            modifier = Modifier.fillMaxWidth()
        )
    }
}