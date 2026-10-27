package dev.sanmer.authenticator.ui.screen.ntp.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.datastore.compose.LocalPreference
import dev.sanmer.authenticator.ui.ktx.bottom
import kotlinx.coroutines.launch

@Composable
fun CustomBottomSheet(
    onClose: () -> Unit,
    ntpAddress: TextFieldState,
    onSave: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) = ModalBottomSheet(
    onDismissRequest = onClose,
    sheetState = sheetState,
    shape = MaterialTheme.shapes.extraLarge.bottom(0.dp),
    dragHandle = null
) {
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    Text(
        text = stringResource(R.string.ntp_custom),
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 30.dp, bottom = 10.dp)
    )

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(15.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        val preference = LocalPreference.current
        val isChanged by remember(ntpAddress) {
            derivedStateOf {
                ntpAddress.text.trim() != preference.ntpAddress
            }
        }

        OutlinedTextField(
            state = ntpAddress,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Done
            ),
            shape = MaterialTheme.shapes.medium,
            lineLimits = TextFieldLineLimits.SingleLine,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                scope.launch { sheetState.hide() }
                    .invokeOnCompletion {
                        keyboardController?.hide()
                        onSave()
                        onClose()
                    }
            },
            enabled = isChanged,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.token_save))
        }
    }
}