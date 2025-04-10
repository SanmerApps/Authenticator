package dev.sanmer.authenticator.ui.screens.settings.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ui.ktx.navigateSingleTopTo
import dev.sanmer.authenticator.ui.main.Screen

@Composable
fun PreferenceItem(
    onDismiss: () -> Unit,
    navController: NavController
) {
    SettingBottomSheet(
        onDismiss = onDismiss,
        title = stringResource(id = R.string.settings_preference)
    ) {
        SettingItem(
            icon = R.drawable.shield,
            title = stringResource(id = R.string.settings_security),
            onClick = {
                navController.navigateSingleTopTo(Screen.Security())
                onDismiss()
            }
        )

        SettingItem(
            icon = R.drawable.timezone,
            title = stringResource(id = R.string.settings_ntp_server),
            onClick = {
                navController.navigateSingleTopTo(Screen.Ntp())
                onDismiss()
            }
        )
    }
}