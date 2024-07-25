package dev.sanmer.authenticator.ui.screens.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ui.component.PageIndicator
import dev.sanmer.authenticator.ui.component.ReversedModalNavigationDrawer
import dev.sanmer.authenticator.ui.component.SearchTopBar
import dev.sanmer.authenticator.ui.screens.home.component.AuthList
import dev.sanmer.authenticator.ui.screens.settings.SettingsScreen
import dev.sanmer.authenticator.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController
) {
    val configuration = LocalConfiguration.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val maxWidth by remember {
        derivedStateOf { configuration.smallestScreenWidthDp * 0.8f }
    }

    BackHandler(
        enabled = drawerState.isOpen,
        onBack = { scope.launch { drawerState.close() } }
    )

    ReversedModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerShape = MaterialTheme.shapes.large,
                windowInsets = WindowInsets(0.dp)
            ) {
                Box(
                    modifier = Modifier.width(maxWidth.dp)
                ) {
                    SettingsScreen(
                        navController = navController,
                        onBack = { scope.launch { drawerState.close() } }
                    )
                }
            }
        }
    ) {
        HomeContent(
            navController = navController,
            onOpen = { scope.launch { drawerState.open() } }
        )
    }
}

@Composable
private fun HomeContent(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController,
    onOpen: () -> Unit,
) {
    val auths by viewModel.auths.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()

    BackHandler(
        enabled = viewModel.isSearch,
        onBack = viewModel::closeSearch
    )

    DisposableEffect(true) {
        onDispose(viewModel::closeSearch)
    }

    Scaffold(
        topBar = {
            TopBar(
                isSearch = viewModel.isSearch,
                enableSearch = auths.isNotEmpty(),
                onQueryChange = viewModel::search,
                onOpenSearch = viewModel::openSearch,
                onCloseSearch = viewModel::closeSearch,
                onOpenDrawer = onOpen,
                scrollBehavior = scrollBehavior
            )
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .imePadding()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            if (auths.isEmpty()) {
                PageIndicator(
                    icon = if (viewModel.isSearch) R.drawable.list_search else R.drawable.list,
                    text = stringResource(id = R.string.empty_list),
                    modifier = Modifier.padding(contentPadding)
                )
            }

            AuthList(
                state = listState,
                navController = navController,
                auths = auths,
                recycleAuth = viewModel::recycleAuth,
                updateAuth = viewModel::updateAuth,
                contentPadding = contentPadding
            )
        }
    }
}

@Composable
private fun TopBar(
    isSearch: Boolean,
    enableSearch: Boolean,
    onQueryChange: (String) -> Unit,
    onOpenSearch: () -> Unit,
    onCloseSearch: () -> Unit,
    onOpenDrawer: () -> Unit,
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
                onClick = onOpenSearch,
                enabled = enableSearch
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = null
                )
            }

            IconButton(
                onClick = onOpenDrawer
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.menu_2),
                    contentDescription = null
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}