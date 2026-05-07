package dev.sanmer.authenticator.ui.screens.settings.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.sanmer.authenticator.R

@Composable
fun PreferenceItem(
    onDismiss: () -> Unit,
    onSecurity: () -> Unit,
    onNtp: () -> Unit
) {
    SettingBottomSheet(
        onDismiss = onDismiss,
        title = stringResource(id = R.string.settings_preference)
    ) {
        SettingItem(
            icon = R.drawable.shield,
            title = stringResource(id = R.string.settings_security),
            onClick = {
                onSecurity()
                onDismiss()
            }
        )

        SettingItem(
            icon = R.drawable.timezone,
            title = stringResource(id = R.string.settings_ntp_server),
            onClick = {
                onNtp()
                onDismiss()
            }
        )
    }
}