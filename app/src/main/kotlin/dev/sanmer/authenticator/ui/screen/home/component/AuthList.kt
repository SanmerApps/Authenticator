package dev.sanmer.authenticator.ui.screen.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.database.model.Auth
import dev.sanmer.authenticator.ui.ktx.plus
import kotlinx.coroutines.flow.StateFlow

@Composable
fun AuthList(
    list: List<Pair<Auth, StateFlow<String>>>,
    onEdit: (Auth) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp)
) = LazyColumn(
    modifier = modifier,
    state = state,
    contentPadding = PaddingValues(15.dp) + contentPadding,
    verticalArrangement = Arrangement.spacedBy(15.dp)
) {
    items(
        items = list,
        key = { (auth, _) -> auth.id }
    ) { (auth, otp) ->
        AuthItem(
            auth = auth,
            otp = otp,
            onClick = { onEdit(auth) }
        )
    }
}