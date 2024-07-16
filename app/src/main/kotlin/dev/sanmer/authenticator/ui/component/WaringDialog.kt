package dev.sanmer.authenticator.ui.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.R

@Composable
fun WaringDialog(
    onOk: () -> Unit,
    onCancel: () -> Unit
) = AlertDialog(
    shape = RoundedCornerShape(20.dp),
    containerColor = MaterialTheme.colorScheme.surface,
    tonalElevation = 0.dp,
    onDismissRequest = onCancel,
    title = { Text(text = stringResource(id = R.string.dialog_warning_title)) },
    text = { Text(text = stringResource(id = R.string.dialog_warning_desc)) },
    dismissButton = {
        TextButton(
            onClick = onCancel
        ) {
            Text(
                text = stringResource(id = R.string.dialog_cancel)
            )
        }
    },
    confirmButton = {
        TextButton(
            onClick = onOk
        ) {
            Text(
                text = stringResource(id = R.string.dialog_ok)
            )
        }
    }
)