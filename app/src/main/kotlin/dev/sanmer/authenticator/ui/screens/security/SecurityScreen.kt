package dev.sanmer.authenticator.ui.screens.security

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ui.AuthorizeActivity
import dev.sanmer.authenticator.ui.provider.LocalPreference
import dev.sanmer.authenticator.ui.screens.security.component.SecurityItem
import dev.sanmer.crypto.BiometricKey

@Composable
fun SecurityScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val preference = LocalPreference.current

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                scrollBehavior = scrollBehavior
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
        ) {
            SecurityItem(
                icon = R.drawable.shield,
                title = stringResource(id = R.string.security_password),
                desc = stringResource(id = if (preference.isEncrypted) {
                    R.string.security_password_mask
                } else {
                    R.string.security_password_empty
                }),
                onClick = {
                    val action = if (preference.isEncrypted) {
                        AuthorizeActivity.Action.ChangePassword
                    } else {
                        AuthorizeActivity.Action.SetupPassword
                    }
                    AuthorizeActivity.start(context, action) {}
                }
            )

            SecurityItem(
                icon = R.drawable.fingerprint,
                title = stringResource(id = R.string.security_biometric),
                desc = stringResource(id = R.string.security_biometric_desc),
                enabled = preference.isEncrypted && BiometricKey.canAuthenticate(context),
                onClick = {
                    val action = if (preference.isBiometric) {
                        AuthorizeActivity.Action.RemoveBiometric
                    } else {
                        AuthorizeActivity.Action.SetupBiometric
                    }
                    AuthorizeActivity.start(context, action) {}
                },
                trailing = {
                    Switch(
                        checked = preference.isBiometric,
                        onCheckedChange = null
                    )
                }
            )

            SecurityItem(
                icon = R.drawable.shield_off,
                title = stringResource(id = R.string.security_remove_password),
                desc = stringResource(id = R.string.security_remove_password_desc),
                enabled = preference.isEncrypted,
                onClick = {
                    val action = AuthorizeActivity.Action.RemovePassword
                    AuthorizeActivity.start(context, action) {}
                }
            )
        }
    }
}

@Composable
private fun TopBar(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    title = { Text(text = stringResource(id = R.string.settings_security)) },
    navigationIcon = {
        IconButton(
            onClick = { navController.navigateUp() }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_left),
                contentDescription = null
            )
        }
    },
    scrollBehavior = scrollBehavior
)