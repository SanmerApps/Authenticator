package dev.sanmer.authenticator.ui.screens.home

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.model.auth.Auth
import dev.sanmer.authenticator.model.auth.HotpAuth
import dev.sanmer.authenticator.model.auth.TotpAuth
import dev.sanmer.authenticator.model.serializer.AuthJson
import dev.sanmer.authenticator.ui.component.PageIndicator
import dev.sanmer.authenticator.ui.component.SearchTopBar
import dev.sanmer.authenticator.ui.ktx.navigateSingleTopTo
import dev.sanmer.authenticator.ui.navigation.graphs.HomeScreen
import dev.sanmer.authenticator.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val auths by viewModel.auths.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                isSearch = viewModel.isSearch,
                onQueryChange = viewModel::search,
                onOpenSearch = {
                    if (auths.isNotEmpty()) viewModel.openSearch()
                },
                onCloseSearch = viewModel::closeSearch,
                isEditing = viewModel.isEditing,
                toggleEditing = {
                    viewModel.updateEditing { if (auths.isNotEmpty()) !it else true }
                },
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .imePadding()
        ) {
            if (viewModel.isSearch && auths.isEmpty()) {
                PageIndicator(
                    icon = R.drawable.list_search,
                    text = stringResource(id = R.string.empty_list)
                )
            }

            AuthList(
                state = listState,
                navController = navController,
                auths = auths,
                isEditing = viewModel.isEditing,
                updateHotp = viewModel::updateHotp,
                importJson = viewModel::importFromJson,
                encrypt = viewModel::encrypt,
                exportJson = viewModel::exportToJson
            )
        }
    }
}

@Composable
private fun AuthList(
    state: LazyListState,
    navController: NavController,
    auths: List<Auth>,
    isEditing: Boolean,
    updateHotp: (HotpAuth) -> Unit,
    importJson: (Context, Uri) -> Unit,
    encrypt: (Context, () -> Unit) -> Unit,
    exportJson: (Context, Uri) -> Unit
) {
    val hotp by remember(auths) {
        derivedStateOf { auths.filterIsInstance<HotpAuth>() }
    }
    val totp by remember(auths) {
        derivedStateOf { auths.filterIsInstance<TotpAuth>() }
    }

    LazyColumn(
        modifier = Modifier.animateContentSize(),
        state = state,
        contentPadding = PaddingValues(15.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        if (isEditing) {
            optionItems(
                importJson = importJson,
                encrypt = encrypt,
                exportJson = exportJson,
                navController = navController,
                enabledExport = auths.isNotEmpty()
            )
        }

        items(totp) {
            OtpItem(
                auth = it,
                enabled = isEditing,
            ) {
                navController.navigateSingleTopTo(HomeScreen.Edit(it.secret))
            }
        }

        items(hotp) {
            DisposableEffect(true) {
                onDispose { updateHotp(it) }
            }

            OtpItem(
                auth = it,
                enabled = true,
            ) {
                if (isEditing) {
                    navController.navigateSingleTopTo(HomeScreen.Edit(it.secret))
                } else {
                    it.new()
                }
            }
        }
    }
}

private fun LazyListScope.optionItems(
    importJson: (Context, Uri) -> Unit,
    encrypt: (Context, () -> Unit) -> Unit,
    exportJson: (Context, Uri) -> Unit,
    navController: NavController,
    enabledExport: Boolean = true
) {
    item {
        ButtonItem(
            icon = R.drawable.pencil_plus,
            title = stringResource(id = R.string.home_enter_title),
            desc = stringResource(id = R.string.home_enter_desc),
            onClick = { navController.navigateSingleTopTo(HomeScreen.Edit()) }
        )
    }

    item {
        ButtonItem(
            icon = R.drawable.scan,
            title = stringResource(id = R.string.home_scant_tile),
            desc = stringResource(id = R.string.home_scant_desc),
            onClick = { navController.navigateSingleTopTo(HomeScreen.Scan.route) }
        )
    }

    item {
        val context = LocalContext.current
        val importJsonLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) importJson(context, uri)
        }

        ButtonItem(
            icon = R.drawable.json,
            title = stringResource(id = R.string.home_import_title),
            desc = stringResource(id = R.string.home_import_desc),
            onClick = { importJsonLauncher.launch(AuthJson.MIME_TYPE) }
        )
    }

    item {
        val context = LocalContext.current
        val exportJsonLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument(AuthJson.MIME_TYPE)
        ) { uri ->
            if (uri != null) exportJson(context, uri)
        }

        ButtonItem(
            icon = R.drawable.json,
            title = stringResource(id = R.string.home_export_title),
            desc = stringResource(id = R.string.home_export_desc),
            onClick = { encrypt(context) { exportJsonLauncher.launch(AuthJson.FILE_NAME) } },
            enabled = enabledExport
        )
    }
}

@Composable
private fun ButtonItem(
    @DrawableRes icon: Int,
    title: String,
    desc: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) = OutlinedCard(
    shape = RoundedCornerShape(15.dp),
    onClick = onClick,
    enabled = enabled
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 18.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(45.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = desc,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun TopBar(
    isSearch: Boolean,
    onQueryChange: (String) -> Unit,
    onOpenSearch: () -> Unit,
    onCloseSearch: () -> Unit,
    isEditing: Boolean,
    toggleEditing: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    var query by remember { mutableStateOf("") }
    DisposableEffect(isSearch) {
        onDispose { query = "" }
    }

    SearchTopBar(
        isSearch = isSearch,
        query = query,
        onQueryChange = {
            onQueryChange(it)
            query = it
        },
        onClose = onCloseSearch,
        title = { Text(text = stringResource(id = R.string.app_name)) },
        actions = {
            if (!isSearch) IconButton(
                onClick = onOpenSearch
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = null
                )
            }

            IconButton(
                onClick = toggleEditing
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isEditing) {
                            R.drawable.writing
                        } else {
                            R.drawable.pencil
                        }
                    ),
                    contentDescription = null
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}