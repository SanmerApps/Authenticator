package dev.sanmer.authenticator.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val openDrawer = { scope.launch { drawerState.open() } }
    val closeDrawer = { scope.launch { drawerState.close() } }

    ReversedModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerShape = MaterialTheme.shapes.large,
                windowInsets = WindowInsets(0.dp)
            ) {
                val maxWidth = LocalConfiguration.current.smallestScreenWidthDp * 0.8f
                Box(
                    modifier = Modifier.width(maxWidth.dp)
                ) {
                    SettingsScreen(
                        navController = navController,
                        onBack = { closeDrawer() }
                    )
                }
            }
        }
    ) {
        HomeContent(
            navController = navController,
            onOpen = { openDrawer() }
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

    Scaffold(
        topBar = {
            TopBar(
                isSearch = viewModel.isSearch,
                onQueryChange = viewModel::search,
                onOpenSearch = {
                    if (auths.isNotEmpty()) viewModel.openSearch()
                },
                onOpenDrawer = onOpen,
                onCloseSearch = viewModel::closeSearch,
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .imePadding()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            if (auths.isEmpty()) {
                PageIndicator(
                    icon = if (viewModel.isSearch) R.drawable.list_search else R.drawable.list,
                    text = stringResource(id = R.string.empty_list)
                )
            }

            AuthList(
                state = listState,
                navController = navController,
                auths = auths,
                recycleAuth = viewModel::recycleAuth,
                updateAuth = viewModel::updateAuth
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
                onClick = onOpenSearch
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