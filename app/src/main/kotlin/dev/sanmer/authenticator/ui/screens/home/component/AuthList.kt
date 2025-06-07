package dev.sanmer.authenticator.ui.screens.home.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sanmer.authenticator.database.entity.TotpEntity
import dev.sanmer.authenticator.model.impl.TotpImpl
import dev.sanmer.authenticator.ui.ktx.navigateSingleTopTo
import dev.sanmer.authenticator.ui.ktx.plus
import dev.sanmer.authenticator.ui.main.Screen

@Composable
fun AuthList(
    state: LazyListState,
    navController: NavController,
    totp: List<TotpImpl>,
    recycle: (TotpEntity) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        state = state,
        contentPadding = contentPadding + PaddingValues(15.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(totp) {
            AuthItem(
                auth = it,
                enabled = false,
                onEdit = { navController.navigateSingleTopTo(Screen.Edit(it.entity.id, "")) },
                onDelete = { recycle(it.entity) }
            )
        }
    }
}