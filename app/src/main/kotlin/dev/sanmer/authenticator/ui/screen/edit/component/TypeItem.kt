package dev.sanmer.authenticator.ui.screen.edit.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.auth.Otp
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.database.model.Auth
import dev.sanmer.authenticator.ui.component.FixedIcon
import dev.sanmer.authenticator.ui.component.IconRow
import kotlin.enums.enumEntries

@Composable
fun TypeItem(
    type: MutableState<Auth.Type>,
    hash: MutableState<Otp.Hash>,
    enabled: Boolean = true
) = IconRow(
    leadingIcon = { FixedIcon(painter = painterResource(R.drawable.math_function)) }
) {
    val (typeExpanded, onTypeExpandedChange) = remember { mutableStateOf(false) }
    val (hashExpanded, onHashExpandedChange) = remember { mutableStateOf(false) }

    EnumTextField(
        value = type.value,
        onValueChange = { type.value = it },
        expanded = typeExpanded,
        onExpandedChange = onTypeExpandedChange,
        label = stringResource(R.string.token_type),
        modifier = Modifier.weight(1f),
        enabled = enabled
    )

    Spacer(modifier = Modifier.width(15.dp))

    EnumTextField(
        value = hash.value,
        onValueChange = { hash.value = it },
        expanded = hashExpanded,
        onExpandedChange = onHashExpandedChange,
        label = stringResource(R.string.token_hash),
        modifier = Modifier.weight(1f),
        enabled = enabled
    )
}

@Composable
private inline fun <reified T : Enum<T>> EnumTextField(
    value: T,
    crossinline onValueChange: (T) -> Unit,
    expanded: Boolean,
    crossinline onExpandedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) = ExposedDropdownMenuBox(
    expanded = expanded,
    onExpandedChange = { onExpandedChange(it) },
    modifier = modifier
) {
    OutlinedTextField(
        value = value.name,
        onValueChange = {},
        enabled = enabled,
        readOnly = true,
        label = { Text(text = label) },
        shape = MaterialTheme.shapes.medium,
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .menuAnchor(
                type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                enabled = enabled
            )
            .padding(bottom = 8.dp)
    )

    ExposedDropdownMenu(
        expanded = expanded,
        onDismissRequest = { onExpandedChange(false) },
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.padding(vertical = 0.dp)
    ) {
        enumEntries<T>().forEachIndexed { index, value ->
            if (index != 0) Spacer(modifier = Modifier.height(4.dp))

            DropdownMenuItem(
                text = {
                    Text(
                        text = value.name,
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                onClick = {
                    onValueChange(value)
                    onExpandedChange(false)
                },
                contentPadding = PaddingValues(horizontal = 8.dp),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clip(shape = MaterialTheme.shapes.small)
            )
        }
    }
}