package dev.sanmer.authenticator.ui.screens.edit.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.model.auth.Auth
import dev.sanmer.otp.HOTP

@Composable
fun TypeItem(
    type: Auth.Type,
    onTypeChange: (Auth.Type) -> Unit,
    hash: HOTP.Hash,
    onHashChange: (HOTP.Hash) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = true
) = BaseContent(
    modifier = modifier,
    leadingIcon = R.drawable.math_function
) {
    BaseDropdownMenu(
        value = type,
        values = Auth.Type.entries,
        onValueChange = onTypeChange,
        label = stringResource(id = R.string.edit_type),
        readOnly = readOnly,
        modifier = Modifier.weight(1f)
    )

    Spacer(modifier = Modifier.width(15.dp))

    BaseDropdownMenu(
        value = hash,
        values = HOTP.Hash.entries,
        onValueChange = onHashChange,
        label = stringResource(id = R.string.edit_hash),
        readOnly = readOnly,
        modifier = Modifier.weight(1f)
    )
}