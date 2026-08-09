package dev.sanmer.authenticator.ui.screen.edit.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.sanmer.auth.encodeBase32
import dev.sanmer.authenticator.BuildConfig
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ui.component.FixedBox
import dev.sanmer.authenticator.ui.component.FixedIcon
import dev.sanmer.authenticator.ui.component.IconRow
import kotlin.random.Random

@Composable
fun SecretItem(
    secret: TextFieldState,
    hidden: MutableState<Boolean>,
    isEdit: Boolean,
    enabled: Boolean = true
) = IconRow(
    leadingIcon = { FixedIcon(painter = painterResource(R.drawable.key)) },
    trailingIcon = {
        if (isEdit) IconButton(
            onClick = { hidden.value = !hidden.value },
            enabled = enabled
        ) {
            Icon(
                painter = painterResource(
                    if (hidden.value) R.drawable.sparkles_2
                    else R.drawable.sparkles_2_off
                ),
                contentDescription = null
            )
        } else if (BuildConfig.DEBUG) IconButton(
            onClick = {
                val value = ByteArray(16).let(Random::nextBytes)
                secret.setTextAndPlaceCursorAtEnd(value.encodeBase32())
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.arrows_shuffle),
                contentDescription = null
            )
        } else FixedBox()
    }
) {
    OutlinedSecureTextField(
        state = secret,
        enabled = enabled,
        label = { Text(text = stringResource(R.string.token_secret)) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        shape = MaterialTheme.shapes.medium,
        textObfuscationMode = with(TextObfuscationMode) { if (hidden.value) Hidden else Visible },
        modifier = Modifier
            .weight(1f)
            .padding(bottom = 8.dp)
    )
}