package dev.sanmer.authenticator.ui.screen.trash

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ui.screen.Screen
import dev.sanmer.authenticator.ui.screen.trash.component.AuthList

@Composable
fun TrashScreen(
    viewModel: TrashViewModel,
    goTo: (Screen) -> Unit,
    goBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    BackHandler(
        enabled = viewModel.isPick,
        onBack = viewModel::clearSelected
    )

    Scaffold(
        topBar = {
            TopBar(
                onBack = {
                    if (viewModel.isPick) {
                        viewModel.clearSelected()
                    } else {
                        goBack()
                    }
                },
                selected = viewModel.selected,
                isPick = viewModel.isPick,
                onDelete = viewModel::delete,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ActionButton(
                onClick = viewModel::restore,
                visible = viewModel.isPick
            )
        }
    ) { contentPadding ->
        viewModel.data.onSuccess { list ->
            AuthList(
                list = list,
                onEdit = {
                    if (viewModel.isPick) {
                        viewModel.pick(it)
                    } else {
                        goTo(Screen.Edit(authId = it.id))
                    }
                },
                isSelected = viewModel::isSelected,
                onPick = viewModel::pick,
                contentPadding = contentPadding,
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            )
        }
    }
}

@Composable
private fun TopBar(
    onBack: () -> Unit,
    selected: Int,
    isPick: Boolean,
    onDelete: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    title = {
        Text(
            text = if (isPick) selected.toString()
            else stringResource(R.string.trash_title)
        )
    },
    navigationIcon = {
        IconButton(
            onClick = onBack
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_left),
                contentDescription = null
            )
        }
    },
    actions = {
        AnimatedVisibility(
            visible = isPick,
            enter = fadeIn() + scaleIn(),
            exit = scaleOut() + fadeOut()
        ) {
            IconButton(
                onClick = onDelete
            ) {
                Icon(
                    painter = painterResource(R.drawable.trash_simple),
                    contentDescription = null
                )
            }
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
            painter = painterResource(R.drawable.arrow_counter_clockwise),
            contentDescription = null
        )
    }
}