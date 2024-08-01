package dev.sanmer.authenticator.ui.screens.settings.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ui.ktx.bottom
import dev.sanmer.authenticator.ui.ktx.navigateSingleTopTo
import dev.sanmer.authenticator.ui.main.Screen

@Composable
fun TokenItem(
    onDismiss: () -> Unit,
    navController: NavController
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.large.bottom(0.dp),
        windowInsets = WindowInsets(0.dp)
    ) {
        val contentPadding = WindowInsets.navigationBars.asPaddingValues()

        Text(
            text = stringResource(id = R.string.settings_token),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Column(
            modifier = Modifier
                .padding(all = 15.dp)
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            SettingItem(
                icon = R.drawable.pencil_plus,
                title = stringResource(id = R.string.settings_enter),
                onClick = {
                    navController.navigateSingleTopTo(Screen.Edit())
                    onDismiss()
                }
            )

            SettingItem(
                icon = R.drawable.scan,
                title = stringResource(id = R.string.settings_scan),
                onClick = {
                    navController.navigateSingleTopTo(Screen.Scan())
                    onDismiss()
                }
            )
        }
    }
}