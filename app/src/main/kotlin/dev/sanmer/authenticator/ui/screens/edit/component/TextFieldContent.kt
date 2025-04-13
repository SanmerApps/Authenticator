package dev.sanmer.authenticator.ui.screens.edit.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ui.component.DropdownMenu

@Composable
fun TextFieldContentIcon(
    @DrawableRes icon: Int
) = Box(
    modifier = Modifier.size(48.dp),
    contentAlignment = Alignment.Center
) {
    Icon(
        painter = painterResource(id = icon),
        contentDescription = null
    )
}

@Composable
fun TextFieldContentSpacer() = Spacer(modifier = Modifier.width(48.dp))

@Composable
fun TextFieldContent(
    modifier: Modifier = Modifier,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit
) = Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically
) {
    if (leading != null) {
        leading()
    } else {
        TextFieldContentSpacer()
    }
    content()
    if (trailing != null) {
        trailing()
    } else {
        TextFieldContentSpacer()
    }
}

@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) = OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    modifier = modifier,
    readOnly = readOnly,
    label = { Text(text = label) },
    trailingIcon = trailingIcon,
    shape = MaterialTheme.shapes.medium,
    isError = isError,
    visualTransformation = visualTransformation,
    keyboardOptions = keyboardOptions,
    singleLine = true,
    interactionSource = interactionSource
)

@Composable
fun <T> TextFieldDropdownMenu(
    value: T,
    values: Collection<T>,
    onValueChange: (T) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                expanded = !expanded
            }
        }
    }

    DropdownMenu(
        modifier = modifier,
        expanded = expanded,
        onDismissRequest = { expanded = false },
        contentAlignment = Alignment.BottomStart,
        offset = DpOffset(0.dp, 3.dp),
        surface = {
            TextField(
                value = value.toString(),
                onValueChange = {},
                label = label,
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    Icon(
                        painter = painterResource(
                            id = if (expanded) {
                                R.drawable.caret_up
                            } else {
                                R.drawable.caret_down
                            }
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                interactionSource = if (readOnly) {
                    remember { MutableInteractionSource() }
                } else {
                    interactionSource
                }
            )
        }
    ) {
        values.forEach { value ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = value.toString(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                onClick = {
                    onValueChange(value)
                    expanded = false
                }
            )
        }
    }
}