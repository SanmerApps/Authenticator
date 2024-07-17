package dev.sanmer.authenticator.ui.component

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import dev.sanmer.authenticator.R

@Composable
fun NavigateUpTopBar(
    title: String,
    navController: NavController,
    modifier: Modifier = Modifier,
    enable: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null
) = NavigateUpTopBar(
    modifier = modifier,
    title = title,
    onBack = { navController.navigateUp() },
    actions = actions,
    windowInsets = windowInsets,
    colors = colors,
    scrollBehavior = scrollBehavior,
    enable = enable
)

@Composable
fun NavigateUpTopBar(
    title: String,
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    enable: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null
) = NavigateUpTopBar(
    modifier = modifier,
    title = title,
    onBack = { (context as Activity).finish() },
    actions = actions,
    windowInsets = windowInsets,
    colors = colors,
    scrollBehavior = scrollBehavior,
    enable = enable
)

@Composable
fun NavigateUpTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    enable: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null
) = NavigateUpTopBar(
    modifier = modifier,
    title = { Text(text = title) },
    onBack = onBack,
    actions = actions,
    windowInsets = windowInsets,
    colors = colors,
    scrollBehavior = scrollBehavior,
    enable = enable
)

@Composable
fun NavigateUpTopBar(
    title: @Composable () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    enable: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null
) = TopAppBar(
    title = title,
    modifier = modifier,
    navigationIcon = {
        IconButton(
            onClick = onBack,
            enabled = enable
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_left),
                contentDescription = null
            )
        }
    },
    actions = actions,
    windowInsets = windowInsets,
    colors = colors,
    scrollBehavior = scrollBehavior
)