package dev.sanmer.authenticator.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.Const
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ktx.viewUrl
import dev.sanmer.authenticator.ui.screens.Screen
import dev.sanmer.authenticator.ui.screens.settings.SettingsViewModel.BottomSheet
import dev.sanmer.authenticator.ui.screens.settings.component.DatabaseItem
import dev.sanmer.authenticator.ui.screens.settings.component.PreferenceItem
import dev.sanmer.authenticator.ui.screens.settings.component.SettingIcon
import dev.sanmer.authenticator.ui.screens.settings.component.SettingItem
import dev.sanmer.authenticator.ui.screens.settings.component.TokenItem
import dev.sanmer.authenticator.ui.screens.settings.component.ToolItem

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    goTo: (Screen) -> Unit,
    goBack: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    when (viewModel.bottomSheet) {
        BottomSheet.Closed -> Unit
        BottomSheet.Token -> TokenItem(
            onDismiss = viewModel::closeBottomSheet,
            onEdit = { goTo(Screen.Edit()) },
            onScan = { goTo(Screen.Scan) }
        )

        BottomSheet.Database -> DatabaseItem(
            onDismiss = viewModel::closeBottomSheet,
            isEmpty = viewModel.isEmpty,
            importJson = viewModel::importJson,
            importUri = viewModel::importUri,
            encrypt = viewModel::encrypt,
            exportJson = viewModel::exportJson,
            exportUri = viewModel::exportUri,
        )

        BottomSheet.Tool -> ToolItem(
            onDismiss = viewModel::closeBottomSheet,
            onEncode = { goTo(Screen.Encode) },
            decryptFromJson = viewModel::decryptFromJson,
            decryptedToJson = viewModel::decryptedToJson,
        )

        BottomSheet.Preference -> PreferenceItem(
            onDismiss = viewModel::closeBottomSheet,
            onSecurity = { goTo(Screen.Security) },
            onNtp = { goTo(Screen.Ntp) }
        )
    }

    Scaffold(
        topBar = {
            TopBar(
                onBack = goBack,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = viewModel.bottomSheet.isClosed,
                enter = fadeIn() + scaleIn(),
                exit = scaleOut() + fadeOut()
            ) {
                ActionButton(
                    gotToTrash = { goTo(Screen.Trash) }
                )
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
                .padding(all = 15.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            SettingItem(
                icon = {
                    SettingIcon(
                        icon = R.drawable.key,
                        color = if (isSystemInDarkTheme()) {
                            colorResource(id = R.color.material_orange_900)
                        } else {
                            colorResource(id = R.color.material_orange_300)
                        }
                    )
                },
                title = stringResource(id = R.string.settings_token),
                text = stringResource(id = R.string.settings_token_desc),
                onClick = { viewModel.updateBottomSheet { BottomSheet.Token } }
            )

            SettingItem(
                icon = {
                    SettingIcon(
                        icon = R.drawable.database,
                        color = if (isSystemInDarkTheme()) {
                            colorResource(id = R.color.material_green_900)
                        } else {
                            colorResource(id = R.color.material_green_300)
                        }
                    )
                },
                title = stringResource(id = R.string.settings_import_export),
                text = stringResource(id = R.string.settings_import_export_desc),
                onClick = { viewModel.updateBottomSheet { BottomSheet.Database } }
            )

            SettingItem(
                icon = {
                    SettingIcon(
                        icon = R.drawable.tool,
                        color = if (isSystemInDarkTheme()) {
                            colorResource(id = R.color.material_blue_900)
                        } else {
                            colorResource(id = R.color.material_blue_300)
                        }
                    )
                },
                title = stringResource(id = R.string.settings_tools),
                text = stringResource(id = R.string.settings_tools_desc),
                onClick = { viewModel.updateBottomSheet { BottomSheet.Tool } }
            )

            SettingItem(
                icon = {
                    SettingIcon(
                        icon = R.drawable.mood_heart,
                        color = if (isSystemInDarkTheme()) {
                            colorResource(id = R.color.material_deep_orange_900)
                        } else {
                            colorResource(id = R.color.material_deep_orange_300)
                        }
                    )
                },
                title = stringResource(id = R.string.settings_preference),
                text = stringResource(id = R.string.settings_preference_desc),
                onClick = { viewModel.updateBottomSheet { BottomSheet.Preference } }
            )
        }
    }
}

@Composable
private fun ActionButton(
    gotToTrash: () -> Unit
) {
    FloatingActionButton(
        onClick = gotToTrash
    ) {
        Icon(
            painter = painterResource(id = R.drawable.trash),
            contentDescription = null
        )
    }
}

@Composable
private fun TopBar(
    onBack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    title = { Text(text = stringResource(id = R.string.settings_title)) },
    navigationIcon = {
        IconButton(
            onClick = onBack
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_left),
                contentDescription = null
            )
        }
    },
    actions = {
        val context = LocalContext.current

        IconButton(
            onClick = { context.viewUrl(Const.GITHUB_URL) }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.brand_github_2),
                contentDescription = null
            )
        }
    },
    scrollBehavior = scrollBehavior
)