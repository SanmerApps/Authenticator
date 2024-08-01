package dev.sanmer.authenticator.ui.screens.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ui.component.PageIndicator
import dev.sanmer.authenticator.ui.component.SearchTopBar
import dev.sanmer.authenticator.ui.ktx.isScrollingUp
import dev.sanmer.authenticator.ui.ktx.navigateSingleTopTo
import dev.sanmer.authenticator.ui.main.Screen
import dev.sanmer.authenticator.ui.screens.home.component.AuthList
import dev.sanmer.authenticator.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val auths by viewModel.auths.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()
    val isScrollingUp by listState.isScrollingUp()

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
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isScrollingUp,
                enter = fadeIn() + scaleIn(),
                exit = scaleOut() + fadeOut(),
                label = "ActionButton"
            ) {
                ActionButton(navController = navController)
            }
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
                    icon = if (viewModel.isSearch) {
                        R.drawable.list_search
                    } else {
                        R.drawable.key
                    },
                    text = stringResource(
                        id = if (viewModel.isSearch) {
                            R.string.empty_list
                        } else {
                            R.string.home_empty
                        }
                    ),
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
private fun ActionButton(
    navController: NavController
) {
    FloatingActionButton(
        onClick = { navController.navigateSingleTopTo(Screen.Settings()) }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.settings_2),
            contentDescription = null
        )
    }
}

@Composable
private fun TopBar(
    isSearch: Boolean,
    enableSearch: Boolean,
    onQueryChange: (String) -> Unit,
    onOpenSearch: () -> Unit,
    onCloseSearch: () -> Unit,
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
        },
        scrollBehavior = scrollBehavior
    )
}