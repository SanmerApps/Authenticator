package dev.sanmer.authenticator.ui.screens.edit.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun TextFieldItem(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    @DrawableRes leadingIcon: Int? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    hidden: Boolean = false,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Next
    )
) = BaseContent(
    modifier = modifier,
    leading = leadingIcon?.let {
        { BaseContentIcon(icon = it) }
    },
    trailing = trailingIcon
) {
    val passwordVisualTransformation = remember { PasswordVisualTransformation() }

    BaseOutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = Modifier.weight(1f),
        readOnly = hidden,
        isError = isError,
        keyboardOptions = keyboardOptions,
        visualTransformation = if (hidden) {
            passwordVisualTransformation
        } else {
            VisualTransformation.None
        },
    )
}