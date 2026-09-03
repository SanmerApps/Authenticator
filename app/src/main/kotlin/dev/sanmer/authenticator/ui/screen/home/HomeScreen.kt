package dev.sanmer.authenticator.ui.screen.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sanmer.auth.ntp.NtpClock
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.datastore.compose.LocalPreference
import dev.sanmer.authenticator.model.LoadData
import dev.sanmer.authenticator.ui.component.AnimatedPoint
import dev.sanmer.authenticator.ui.component.Point
import dev.sanmer.authenticator.ui.ktx.isScrollingUp
import dev.sanmer.authenticator.ui.screen.Screen
import dev.sanmer.authenticator.ui.screen.home.component.AuthList

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    goTo: (Screen) -> Unit
) {
    val clock by viewModel.clock.collectAsStateWithLifecycle()
    val time by viewModel.time.collectAsStateWithLifecycle("")

    val preference = LocalPreference.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()
    val isScrollingUp by listState.isScrollingUp()

    Scaffold(
        topBar = {
            TopBar(
                time = time,
                clock = clock,
                onSync = { viewModel.sync(preference.ntpServer()) },
                onSetting = { goTo(Screen.Setting) },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ActionButton(
                onClick = { goTo(Screen.Edit()) },
                visible = isScrollingUp,
            )
        }
    ) { contentPadding ->
        viewModel.data.onSuccess { list ->
            AuthList(
                list = list,
                state = listState,
                onEdit = { goTo(Screen.Edit(authId = it.id)) },
                contentPadding = contentPadding,
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            )
        }
    }
}

@Composable
private fun TopBar(
    time: String,
    clock: LoadData<NtpClock>,
    onSync: () -> Unit,
    onSetting: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    title = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = time)

            AnimatedContent(
                targetState = clock,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                contentAlignment = Alignment.Center
            ) {
                when (it) {
                    LoadData.Loading -> AnimatedPoint(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    )

                    is LoadData.Failure -> Point(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onSync),
                        color = MaterialTheme.colorScheme.errorContainer
                    )

                    else -> Spacer(
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    },
    actions = {
        IconButton(
            onClick = onSetting
        ) {
            Icon(
                painter = painterResource(R.drawable.gear),
                contentDescription = null
            )
        }
    },
    scrollBehavior = scrollBehavior
)

@Composable
private fun ActionButton(
    onClick: () -> Unit,
    visible: Boolean = true
) = AnimatedVisibility(
    visible = visible,
    enter = fadeIn() + scaleIn(),
    exit = scaleOut() + fadeOut()
) {
    FloatingActionButton(
        onClick = onClick,
        shape = CircleShape
    ) {
        Icon(
            painter = painterResource(R.drawable.plus),
            contentDescription = null
        )
    }
}