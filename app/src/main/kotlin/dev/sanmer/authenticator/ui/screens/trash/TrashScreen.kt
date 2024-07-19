package dev.sanmer.authenticator.ui.screens.trash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ui.component.NavigateUpTopBar
import dev.sanmer.authenticator.ui.component.PageIndicator
import dev.sanmer.authenticator.ui.screens.trash.component.AuthList
import dev.sanmer.authenticator.viewmodel.TrashViewModel

@Composable
fun TrashScreen(
    viewModel: TrashViewModel = hiltViewModel(),
    navController: NavController
) {
    val auths by viewModel.auths.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                scrollBehavior = scrollBehavior
            )
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            if (auths.isEmpty()) {
                PageIndicator(
                    icon = R.drawable.trash,
                    text = stringResource(id = R.string.empty_list),
                    modifier = Modifier.padding(contentPadding)
                )
            }

            AuthList(
                state = listState,
                auths = auths,
                restoreAuth = viewModel::restoreAuth,
                deleteAuth = viewModel::deleteAuth,
                contentPadding = contentPadding
            )
        }
    }
}

@Composable
private fun TopBar(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior
) = NavigateUpTopBar(
    title = stringResource(id = R.string.settings_trash),
    navController = navController,
    scrollBehavior = scrollBehavior
)