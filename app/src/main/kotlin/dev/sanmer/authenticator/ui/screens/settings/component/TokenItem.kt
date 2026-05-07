package dev.sanmer.authenticator.ui.screens.settings.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.sanmer.authenticator.R

@Composable
fun TokenItem(
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onScan: () -> Unit
) {
    SettingBottomSheet(
        onDismiss = onDismiss,
        title = stringResource(id = R.string.settings_token)
    ) {
        SettingItem(
            icon = R.drawable.edit,
            title = stringResource(id = R.string.settings_enter),
            onClick = {
                onEdit()
                onDismiss()
            }
        )

        SettingItem(
            icon = R.drawable.scan,
            title = stringResource(id = R.string.settings_scan),
            onClick = {
                onScan()
                onDismiss()
            }
        )
    }
}