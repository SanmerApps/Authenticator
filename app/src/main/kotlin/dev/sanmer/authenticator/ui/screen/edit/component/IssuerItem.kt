package dev.sanmer.authenticator.ui.screen.edit.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import dev.sanmer.authenticator.ui.component.FixedBox
import dev.sanmer.authenticator.ui.component.IconRow
import dev.sanmer.brand.Brand

@Composable
fun IssuerItem(
    issuer: TextFieldState,
    brand: Brand?,
    onMatchesBrand: () -> Unit,
    enabled: Boolean = true
) = IconRow(
    leadingIcon = {
        FixedBox {
            if (brand != null) Image(
                painter = painterResource(brand.id),
                contentDescription = null
            )
        }
    },
    trailingIcon = {
        IconButton(
            onClick = onMatchesBrand
        ) {
            Icon(
                painter = painterResource(R.drawable.magnifying_glass),
                contentDescription = null
            )
        }
    }
) {
    OutlinedTextField(
        state = issuer,
        enabled = enabled,
        label = { Text(text = stringResource(R.string.token_issuer)) },
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