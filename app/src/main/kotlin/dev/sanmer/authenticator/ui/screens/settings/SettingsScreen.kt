package dev.sanmer.authenticator.ui.screens.settings

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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sanmer.authenticator.Const
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ktx.viewUrl
import dev.sanmer.authenticator.ui.ktx.navigateSingleTopTo
import dev.sanmer.authenticator.ui.main.Screen
import dev.sanmer.authenticator.ui.screens.settings.component.DatabaseItem
import dev.sanmer.authenticator.ui.screens.settings.component.SettingIcon
import dev.sanmer.authenticator.ui.screens.settings.component.SettingItem
import dev.sanmer.authenticator.ui.screens.settings.component.TokenItem
import dev.sanmer.authenticator.ui.screens.settings.component.ToolItem
import dev.sanmer.authenticator.viewmodel.SettingsViewModel
import dev.sanmer.otp.OtpUri.Default.isOtpUri

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    navController: NavController
) {
    val uri by viewModel.uri.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    DisposableEffect(uri) {
        if (uri.isOtpUri()) navController.navigateSingleTopTo(Screen.Edit(uri))
        onDispose(viewModel::rewind)
    }

    var token by rememberSaveable { mutableStateOf(false) }
    if (token) TokenItem(
        onDismiss = { token = false },
        navController = navController,
        scanImage = viewModel::scanImage,
    )

    var database by rememberSaveable { mutableStateOf(false) }
    if (database) DatabaseItem(
        onDismiss = { database = false },
        encrypt = viewModel::encrypt,
        importFromJson = viewModel::importFromJson,
        exportToJson = viewModel::exportToJson
    )

    var tool by rememberSaveable { mutableStateOf(false) }
    if (tool) ToolItem(
        onDismiss = { tool = false },
        navController = navController,
        decryptedToJson = viewModel::decryptedToJson,
        decryptFromJson = viewModel::decryptFromJson
    )

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ActionButton(navController = navController)
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
                            colorResource(id = R.color.material_blue_900)
                        } else {
                            colorResource(id = R.color.material_blue_300)
                        }
                    )
                },
                title = stringResource(id = R.string.settings_token),
                text = stringResource(id = R.string.settings_token_desc),
                onClick = { token = true }
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
                onClick = { database = true }
            )

            SettingItem(
                icon = {
                    SettingIcon(
                        icon = R.drawable.tool,
                        color = if (isSystemInDarkTheme()) {
                            colorResource(id = R.color.material_orange_900)
                        } else {
                            colorResource(id = R.color.material_orange_300)
                        }
                    )
                },
                title = stringResource(id = R.string.settings_tools),
                text = stringResource(id = R.string.settings_tools_desc),
                onClick = { tool = true }
            )
        }
    }
}

@Composable
private fun ActionButton(
    navController: NavController
) {
    FloatingActionButton(
        onClick = { navController.navigateSingleTopTo(Screen.Trash()) }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.trash),
            contentDescription = null
        )
    }
}

@Composable
private fun TopBar(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    title = { Text(text = stringResource(id = R.string.settings_title)) },
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
    actions = {
        val context = LocalContext.current

        IconButton(
            onClick = { context.viewUrl(Const.GITHUB_URL) }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.brand_github),
                contentDescription = null
            )
        }
    },
    scrollBehavior = scrollBehavior
)