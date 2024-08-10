package dev.sanmer.authenticator.ui.screens.trash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
                onRestore = viewModel::restoreAuthAll,
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
                    text = stringResource(id = R.string.trash_empty),
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
    onRestore: () -> Unit,
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    title = { Text(text = stringResource(id = R.string.trash_title)) },
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
        IconButton(
            onClick = onRestore
        ) {
            Icon(
                painter = painterResource(id = R.drawable.restore),
                contentDescription = null
            )
        }
    },
    scrollBehavior = scrollBehavior
)