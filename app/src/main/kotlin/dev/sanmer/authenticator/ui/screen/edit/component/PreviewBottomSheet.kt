package dev.sanmer.authenticator.ui.screen.edit.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.database.model.AuthProperties
import dev.sanmer.authenticator.ui.component.DragHandle
import dev.sanmer.authenticator.ui.component.Finished
import dev.sanmer.authenticator.ui.ktx.bottom
import dev.sanmer.authenticator.ui.screen.home.component.AuthItem
import kotlinx.coroutines.flow.StateFlow

@Composable
fun PreviewBottomSheet(
    onClose: () -> Unit,
    preview: Result<Pair<AuthProperties, StateFlow<String>>>,
    onSave: (AuthProperties) -> Unit
) = ModalBottomSheet(
    onDismissRequest = onClose,
    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    shape = MaterialTheme.shapes.large.bottom(0.dp),
    dragHandle = null
) {
    DragHandle()

    Text(
        text = stringResource(R.string.token_preview),
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(15.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        preview.onSuccess { (auth, otp) ->
            AuthItem(
                auth = auth.auth,
                otp = otp
            )

            Button(
                onClick = { onSave(auth) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.token_save))
            }
        }.onFailure {
            Finished(
                label = it.message ?: it.javaClass.name,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
            )

            Button(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.token_close))
            }
        }
    }
}