package dev.sanmer.authenticator.ui.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.model.serializer.AuthJson
import dev.sanmer.authenticator.ui.ktx.navigateSingleTopTo
import dev.sanmer.authenticator.ui.main.Screen
import dev.sanmer.authenticator.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    navController: NavController,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val jsonImport = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) viewModel.importFromJson(context, uri)
    }

    val jsonExport = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument(AuthJson.MIME_TYPE)
    ) { uri ->
        if (uri != null) viewModel.exportToJson(context, uri)
    }

    val jsonDecrypt = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument(AuthJson.MIME_TYPE)
    ) { uri ->
        if (uri != null) viewModel.decryptToJson(context, uri)
    }

    val jsonEncrypt = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) viewModel.encryptFromJson(context, uri) {
            jsonDecrypt.launch(AuthJson.FILE_NAME)
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                onBack = onBack,
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
                .padding(all = 15.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SettingItem(
                icon = R.drawable.pencil_plus,
                title = stringResource(id = R.string.settings_enter),
                onClick = { navController.navigateSingleTopTo(Screen.Edit()) }
            )

            SettingItem(
                icon = R.drawable.scan,
                title = stringResource(id = R.string.settings_scan),
                onClick = { navController.navigateSingleTopTo(Screen.Scan.route) }
            )

            SettingItem(
                icon = R.drawable.database_import,
                title = stringResource(id = R.string.settings_import),
                onClick = { jsonImport.launch(AuthJson.MIME_TYPE) }
            )

            SettingItem(
                icon = R.drawable.database_export,
                title = stringResource(id = R.string.settings_export),
                onClick = {
                    viewModel.encrypt(context) {
                        jsonExport.launch(AuthJson.FILE_NAME)
                    }
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            SettingItem(
                icon = R.drawable.trash,
                title = stringResource(id = R.string.settings_trash),
                onClick = { navController.navigateSingleTopTo(Screen.Trash.route) }
            )

            SettingItem(
                icon = R.drawable.clear_all,
                title = stringResource(id = R.string.settings_clear_all),
                onClick = viewModel::recycleAuthAll
            )

            SettingItem(
                icon = R.drawable.arrow_back_up,
                title = stringResource(id = R.string.settings_restore_all),
                onClick = viewModel::restoreAuthAll
            )

            Spacer(modifier = Modifier.height(10.dp))

            SettingItem(
                icon = R.drawable.lock_open,
                title = stringResource(id = R.string.settings_decrypt),
                onClick = { jsonEncrypt.launch(AuthJson.MIME_TYPE) }
            )

            SettingItem(
                icon = R.drawable.a_b,
                title = stringResource(id = R.string.settings_encode_decode),
                onClick = { navController.navigateSingleTopTo(Screen.Encode.route) }
            )
        }
    }
}

@Composable
private fun SettingItem(
    @DrawableRes icon: Int,
    title: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) = Card(
    shape = MaterialTheme.shapes.medium,
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)
    ),
    onClick = onClick,
    enabled = enabled
) {
    Row(
        modifier = Modifier
            .padding(all = 15.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null
        )

        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
fun TopBar(
    onBack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) = CenterAlignedTopAppBar(
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
    scrollBehavior = scrollBehavior
)