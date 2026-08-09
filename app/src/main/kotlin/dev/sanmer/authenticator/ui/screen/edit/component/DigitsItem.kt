package dev.sanmer.authenticator.ui.screen.edit.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.then
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ui.component.IconRow

@Composable
fun DigitsItem(
    digits: TextFieldState,
    period: TextFieldState,
    enabled: Boolean = true
) = IconRow {
    OutlinedTextField(
        state = digits,
        enabled = enabled,
        inputTransformation = InputTransformation.maxLength(2).then {
            if (!asCharSequence().isDigitsOnly()) {
                revertAllChanges()
            }
        },
        label = { Text(text = stringResource(R.string.token_digits)) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        shape = MaterialTheme.shapes.medium,
        lineLimits = TextFieldLineLimits.SingleLine,
        modifier = Modifier
            .weight(1f)
            .padding(bottom = 8.dp)
    )

    Spacer(modifier = Modifier.width(15.dp))

    OutlinedTextField(
        state = period,
        enabled = enabled,
        inputTransformation = InputTransformation.maxLength(2).then {
            if (!asCharSequence().isDigitsOnly()) {
                revertAllChanges()
            }
        },
        label = { Text(text = stringResource(R.string.token_period)) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        shape = MaterialTheme.shapes.medium,
        lineLimits = TextFieldLineLimits.SingleLine,
        modifier = Modifier
            .weight(1f)
            .padding(bottom = 8.dp)
    )
}