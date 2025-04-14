package dev.sanmer.authenticator.ui.screens.home

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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ui.component.LabelText
import dev.sanmer.authenticator.ui.component.PageIndicator
import dev.sanmer.authenticator.ui.ktx.isScrollingUp
import dev.sanmer.authenticator.ui.ktx.navigateSingleTopTo
import dev.sanmer.authenticator.ui.main.Screen
import dev.sanmer.authenticator.ui.screens.home.component.AuthList
import dev.sanmer.authenticator.viewmodel.HomeViewModel
import java.time.LocalTime

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val time by viewModel.time.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()
    val isScrollingUp by listState.isScrollingUp()

    Scaffold(
        topBar = {
            TopBar(
                time = time,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isScrollingUp,
                enter = fadeIn() + scaleIn(),
                exit = scaleOut() + fadeOut()
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
            if (viewModel.totp.isEmpty() && !viewModel.isPending) {
                PageIndicator(
                    icon = R.drawable.key,
                    text = stringResource(id = R.string.home_empty),
                    modifier = Modifier.padding(contentPadding)
                )
            }

            AuthList(
                state = listState,
                navController = navController,
                totp = viewModel.totp,
                recycle = viewModel::recycle,
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
    time: LocalTime,
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    title = { Text(text = stringResource(id = R.string.app_name)) },
    actions = {
        LabelText(
            text = time.toString(),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(end = 15.dp)
        )
    },
    scrollBehavior = scrollBehavior
)