package dev.sanmer.authenticator.ui.screens.edit.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.model.auth.Auth

@Composable
fun DigitsItem(
    type: Auth.Type,
    digits: String,
    onDigitsChange: (String) -> Unit,
    counter: String,
    onCounterChange: (String) -> Unit,
    period: String,
    onPeriodChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = true
) = BaseItem(
    modifier = modifier
) {
    BaseOutlinedTextField(
        value = digits,
        onValueChange = onDigitsChange,
        label = stringResource(id = R.string.edit_digits),
        modifier = Modifier.weight(1f),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        readOnly = readOnly
    )

    Spacer(modifier = Modifier.width(15.dp))

    BaseOutlinedTextField(
        value = when (type) {
            Auth.Type.HOTP -> counter
            Auth.Type.TOTP -> period
        },
        onValueChange = when (type) {
            Auth.Type.HOTP -> onCounterChange
            Auth.Type.TOTP -> onPeriodChange
        },
        label = when (type) {
            Auth.Type.HOTP -> stringResource(id = R.string.edit_counter)
            Auth.Type.TOTP -> stringResource(id = R.string.edit_period)
        },
        modifier = Modifier.weight(1f),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        readOnly = readOnly
    )
}