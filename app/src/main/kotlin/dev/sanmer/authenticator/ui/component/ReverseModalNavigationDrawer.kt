package dev.sanmer.authenticator.ui.component

import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import dev.sanmer.authenticator.ui.ktx.asReversed

@Composable
fun ReversedModalNavigationDrawer(
    drawerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    gesturesEnabled: Boolean = true,
    scrimColor: Color = DrawerDefaults.scrimColor,
    content: @Composable () -> Unit
) = ReversedLayoutDirection {
    ModalNavigationDrawer(
        drawerContent = {
            ReversedLayoutDirection(
                content = drawerContent
            )
        },
        modifier = modifier,
        drawerState = drawerState,
        gesturesEnabled = gesturesEnabled,
        scrimColor = scrimColor,
        content = {
            ReversedLayoutDirection(
                content = content
            )
        }
    )
}

@Composable
private fun ReversedLayoutDirection(
    content: @Composable () -> Unit
) {
    val reverseDirection = LocalLayoutDirection.current.asReversed()
    CompositionLocalProvider(LocalLayoutDirection provides reverseDirection) {
        content()
    }
}