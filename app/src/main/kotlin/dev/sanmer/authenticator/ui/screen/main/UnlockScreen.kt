package dev.sanmer.authenticator.ui.screen.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.datastore.model.Preference

@Composable
fun UnlockScreen(
    viewModel: MainViewModel,
    preference: Preference
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedSecureTextField(
        state = viewModel.password,
        isError = viewModel.isError,
        textObfuscationMode = TextObfuscationMode.Hidden,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Go
        ),
        onKeyboardAction = {
            keyboardController?.hide()
            viewModel.decryptByPassword(preference.keyEncryptedByPassword)
        },
        shape = MaterialTheme.shapes.medium
    )

    Row(
        modifier = Modifier.padding(top = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        if (preference.isBiometric) {
            DisposableEffect(Unit) {
                viewModel.decryptByBiometric(preference.keyEncryptedByBiometric)
                onDispose {}
            }

            FilledTonalIconButton(
                onClick = { viewModel.decryptByBiometric(preference.keyEncryptedByBiometric) }
            ) {
                Icon(
                    painter = painterResource(R.drawable.fingerprint),
                    contentDescription = null
                )
            }
        }

        FilledTonalIconButton(
            onClick = {
                keyboardController?.hide()
                viewModel.decryptByPassword(preference.keyEncryptedByPassword)
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_right),
                contentDescription = null
            )
        }
    }
}