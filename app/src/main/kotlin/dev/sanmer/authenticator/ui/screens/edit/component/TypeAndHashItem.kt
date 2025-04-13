package dev.sanmer.authenticator.ui.screens.edit.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.model.AuthType
import dev.sanmer.otp.HOTP

@Composable
fun TypeAndHashItem(
    type: AuthType,
    onTypeChange: (AuthType) -> Unit,
    hash: HOTP.Hash,
    onHashChange: (HOTP.Hash) -> Unit,
    readOnly: Boolean
) = TextFieldContent(
    leading = { TextFieldContentIcon(icon = R.drawable.math_function) }
) {
    TextFieldDropdownMenu(
        value = type,
        values = AuthType.entries,
        onValueChange = onTypeChange,
        label = stringResource(id = R.string.edit_type),
        readOnly = readOnly,
        modifier = Modifier.weight(1f)
    )

    Spacer(modifier = Modifier.width(15.dp))

    TextFieldDropdownMenu(
        value = hash,
        values = HOTP.Hash.entries,
        onValueChange = onHashChange,
        label = stringResource(id = R.string.edit_hash),
        readOnly = readOnly,
        modifier = Modifier.weight(1f)
    )
}