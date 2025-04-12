package dev.sanmer.authenticator.ui.screens.trash.component

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
import dev.sanmer.authenticator.database.entity.TotpEntity
import dev.sanmer.authenticator.ui.ktx.plus

@Composable
fun AuthList(
    state: LazyListState,
    totp: List<TotpEntity>,
    restore: (TotpEntity) -> Unit,
    delete: (TotpEntity) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) = LazyColumn(
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
            entity = it,
            onRestore = { restore(it) },
            onDelete = { delete(it) }
        )
    }
}