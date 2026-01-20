package dev.sanmer.authenticator.ui.screens.settings.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ui.ktx.navigateSingleTopTo
import dev.sanmer.authenticator.ui.main.Screen

@Composable
fun TokenItem(
    onDismiss: () -> Unit,
    navController: NavController,
) {
    SettingBottomSheet(
        onDismiss = onDismiss,
        title = stringResource(id = R.string.settings_token)
    ) {
        SettingItem(
            icon = R.drawable.edit,
            title = stringResource(id = R.string.settings_enter),
            onClick = {
                navController.navigateSingleTopTo(Screen.Edit(-1, ""))
                onDismiss()
            }
        )

        SettingItem(
            icon = R.drawable.scan,
            title = stringResource(id = R.string.settings_scan),
            onClick = {
                navController.navigateSingleTopTo(Screen.Scan)
                onDismiss()
            }
        )
    }
}