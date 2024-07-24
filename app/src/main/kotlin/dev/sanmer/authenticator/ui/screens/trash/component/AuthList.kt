package dev.sanmer.authenticator.ui.screens.trash.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.model.auth.Auth
import dev.sanmer.authenticator.ui.ktx.plus
import dev.sanmer.authenticator.viewmodel.TrashViewModel

@Composable
fun AuthList(
    state: LazyListState,
    auths: List<TrashViewModel.AuthWrapper>,
    restoreAuth: (Auth) -> Unit,
    deleteAuth: (Auth) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) = LazyColumn(
    modifier = Modifier.animateContentSize(),
    state = state,
    contentPadding = contentPadding + PaddingValues(15.dp),
    verticalArrangement = Arrangement.spacedBy(15.dp),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    items(auths) {
        AuthItem(
            auth = it.auth,
            lifetime = it.lifetime,
            onRestore = { restoreAuth(it.auth) },
            onDelete = { deleteAuth(it.auth) }
        )
    }
}