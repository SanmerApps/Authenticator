package dev.sanmer.authenticator.ui.screen.setting

import android.content.Intent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.Const
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.crypto.BiometricKey
import dev.sanmer.authenticator.datastore.compose.LocalPreference
import dev.sanmer.authenticator.ui.ktx.bottomWith
import dev.sanmer.authenticator.ui.ktx.plus
import dev.sanmer.authenticator.ui.ktx.topWith
import dev.sanmer.authenticator.ui.screen.Screen
import dev.sanmer.authenticator.ui.screen.ntp.component.name
import dev.sanmer.authenticator.ui.screen.setting.SettingViewModel.BottomSheet
import dev.sanmer.authenticator.ui.screen.setting.component.PasswordBottomSheet
import dev.sanmer.authenticator.ui.screen.setting.component.SettingColumn
import dev.sanmer.authenticator.ui.screen.setting.component.SettingItem
import dev.sanmer.brand.Brand

@Composable
fun SettingScreen(
    viewModel: SettingViewModel,
    goTo: (Screen) -> Unit,
    goBack: () -> Unit
) {
    val preference = LocalPreference.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    when (viewModel.bottomSheet) {
        BottomSheet.None -> {}
        BottomSheet.Password -> PasswordBottomSheet(
            onClose = { viewModel.bottomSheet = BottomSheet.None },
            password = viewModel.password,
            hidden = viewModel.hidden,
            onSet = viewModel::setupPassword,
            onRemove = viewModel::removePassword,
            onChange = viewModel::changePassword
        )
    }

    Scaffold(
        topBar = {
            TopBar(
                onBack = goBack,
                scrollBehavior = scrollBehavior
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .animateContentSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(PaddingValues(15.dp) + contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            SettingColumn(
                title = stringResource(R.string.setting_security)
            ) {
                SettingItem(
                    onClick = { viewModel.bottomSheet = BottomSheet.Password },
                    title = stringResource(R.string.setting_password),
                    value = stringResource(
                        if (preference.isEncrypted) R.string.setting_password_set
                        else R.string.setting_password_not_set
                    ),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(
                                if (preference.isEncrypted) R.drawable.lock
                                else R.drawable.lock_open
                            ),
                            contentDescription = null
                        )
                    },
                    shape = with(MaterialTheme.shapes) { large bottomWith extraSmall }
                )

                if (preference.isEncrypted && BiometricKey.isInitialized) SettingItem(
                    onClick = if (preference.isBiometric) {
                        viewModel::removeBiometric
                    } else {
                        viewModel::setupBiometric
                    },
                    title = stringResource(R.string.setting_biometric),
                    value = stringResource(R.string.setting_biometric_desc),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.fingerprint_simple),
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        Switch(
                            checked = preference.isBiometric,
                            onCheckedChange = null
                        )
                    }
                )

                SettingItem(
                    onClick = { viewModel.setSecureWindow(!preference.secureWindow) },
                    title = stringResource(R.string.setting_prevent_screenshot),
                    value = stringResource(R.string.setting_prevent_screenshot_desc),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.detective),
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        Switch(
                            checked = preference.secureWindow,
                            onCheckedChange = null
                        )
                    },
                    shape = with(MaterialTheme.shapes) { large topWith extraSmall }
                )
            }

            SettingColumn(
                title = stringResource(R.string.setting_database)
            ) {
                SettingItem(
                    onClick = { goTo(Screen.Trash) },
                    title = stringResource(R.string.trash_title),
                    value = if (viewModel.isTrashNotEmpty) viewModel.trashed.toString()
                    else stringResource(R.string.trash_empty),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(
                                if (viewModel.isTrashNotEmpty) R.drawable.trash
                                else R.drawable.trash_simple
                            ),
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.caret_right),
                            contentDescription = null
                        )
                    },
                    enabled = viewModel.isTrashNotEmpty,
                    shape = with(MaterialTheme.shapes) { large bottomWith extraSmall }
                )

                SettingItem(
                    onClick = { goTo(Screen.Export) },
                    title = stringResource(R.string.setting_import_export),
                    value = stringResource(R.string.setting_import_export_desc),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.database),
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.caret_right),
                            contentDescription = null
                        )
                    },
                    shape = with(MaterialTheme.shapes) { large topWith extraSmall }
                )
            }

            SettingColumn(
                title = stringResource(R.string.setting_other)
            ) {
                SettingItem(
                    onClick = { goTo(Screen.Ntp) },
                    title = stringResource(R.string.ntp_title),
                    value = preference.ntp.name(),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.hourglass),
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.caret_right),
                            contentDescription = null
                        )
                    },
                    shape = with(MaterialTheme.shapes) { large bottomWith extraSmall }
                )

                SettingItem(
                    onClick = { goTo(Screen.Brand) },
                    title = stringResource(R.string.setting_brand),
                    value = Brand.entries.size.toString(),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.copyright),
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.caret_right),
                            contentDescription = null
                        )
                    },
                    shape = with(MaterialTheme.shapes) { large topWith extraSmall }
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    onBack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    title = { Text(text = stringResource(R.string.setting_title)) },
    navigationIcon = {
        IconButton(
            onClick = onBack
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_left),
                contentDescription = null
            )
        }
    },
    actions = {
        val context = LocalContext.current
        IconButton(
            onClick = {
                context.startActivity(
                    Intent.parseUri(Const.GITHUB_URL, Intent.URI_INTENT_SCHEME)
                )
            }
        ) {
            Icon(
                painter = painterResource(dev.sanmer.brand.R.drawable.brand_github),
                contentDescription = null
            )
        }
    },
    scrollBehavior = scrollBehavior
)