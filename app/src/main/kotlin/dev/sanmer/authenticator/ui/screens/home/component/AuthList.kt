package dev.sanmer.authenticator.ui.screens.home.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sanmer.authenticator.model.auth.Auth
import dev.sanmer.authenticator.model.auth.HotpAuth
import dev.sanmer.authenticator.model.auth.TotpAuth
import dev.sanmer.authenticator.ui.ktx.navigateSingleTopTo
import dev.sanmer.authenticator.ui.ktx.plus
import dev.sanmer.authenticator.ui.main.Screen

@Composable
fun AuthList(
    state: LazyListState,
    navController: NavController,
    auths: List<Auth>,
    recycleAuth: (Auth) -> Unit,
    updateAuth: (Auth) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val hotp by remember(auths) {
        derivedStateOf { auths.filterIsInstance<HotpAuth>() }
    }
    val totp by remember(auths) {
        derivedStateOf { auths.filterIsInstance<TotpAuth>() }
    }

    LazyColumn(
        modifier = Modifier.animateContentSize(),
        state = state,
        contentPadding = contentPadding + PaddingValues(15.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(totp) {
            AuthItem(
                auth = it,
                enabled = false,
                onEdit = { navController.navigateSingleTopTo(Screen.Edit(it.secret)) },
                onDelete = { recycleAuth(it) }
            )
        }

        items(hotp) {
            DisposableEffect(true) {
                onDispose { updateAuth(it) }
            }

            AuthItem(
                auth = it,
                enabled = true,
                onClick = { it.new() },
                onEdit = { navController.navigateSingleTopTo(Screen.Edit(it.secret)) },
                onDelete = { recycleAuth(it) }
            )
        }
    }
}