package dev.sanmer.authenticator.ui.screens.authorize.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.R

@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    password: String,
    onPasswordChange: (String) -> Unit,
    imeAction: ImeAction = ImeAction.Done,
    onImeDone: () -> Unit = {},
    title: String? = null,
    isError: Boolean = false,
    placeholder: (@Composable () -> Unit)? = null,
    @DrawableRes actionIcon: Int? = null,
    onActionClick: (() -> Unit)? = null
) {
    var hidden by remember { mutableStateOf(true) }
    val passwordVisualTransformation = remember { PasswordVisualTransformation() }

    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        }

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            shape = MaterialTheme.shapes.medium,
            singleLine = true,
            isError = isError,
            modifier = modifier,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onImeDone()
                    defaultKeyboardAction(ImeAction.Done)
                }
            ),
            visualTransformation = if (hidden) {
                passwordVisualTransformation
            } else {
                VisualTransformation.None
            },
            placeholder = placeholder,
            trailingIcon = {
                IconButton(
                    onClick = onActionClick ?: {
                        hidden = !hidden
                    }
                ) {
                    Icon(
                        painter = painterResource(
                            id = actionIcon ?: if (hidden) {
                                R.drawable.eye_closed
                            } else {
                                R.drawable.eye
                            }
                        ),
                        contentDescription = null
                    )
                }
            }
        )
    }
}