package dev.sanmer.authenticator.ui.screen.edit.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ui.component.FixedIcon
import dev.sanmer.authenticator.ui.component.IconRow

@Composable
fun NameItem(
    name: TextFieldState,
    enabled: Boolean = true
) = IconRow(
    leadingIcon = { FixedIcon(painter = painterResource(R.drawable.user)) }
) {
    OutlinedTextField(
        state = name,
        enabled = enabled,
        label = { Text(text = stringResource(R.string.token_name)) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        shape = MaterialTheme.shapes.medium,
        lineLimits = TextFieldLineLimits.SingleLine,
        modifier = Modifier
            .weight(1f)
            .padding(bottom = 8.dp)
    )
}