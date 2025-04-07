package dev.sanmer.authenticator.ui.screens.crypto

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ktx.finishActivity
import dev.sanmer.authenticator.ui.screens.authorize.component.PasswordTextField
import dev.sanmer.authenticator.viewmodel.CryptoViewModel

@Composable
fun CryptoScreen(
    viewModel: CryptoViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    BackHandler {
        viewModel.rewind()
        context.finishActivity()
    }

    DisposableEffect(viewModel.state) {
        if (viewModel.state.isOk) context.finishActivity()
        onDispose {}
    }

    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PasswordTextField(
            password = viewModel.password,
            onPasswordChange = viewModel::updatePassword,
            isError = viewModel.state.isFailed,
            placeholder = { Text(text = stringResource(id = R.string.security_password)) },
            modifier = Modifier.width(TextFieldDefaults.MinWidth)
        )

        Spacer(modifier = Modifier.height(45.dp))

        FilledTonalButton(
            onClick = {
                if (viewModel.crypto()) {
                    context.finishActivity()
                }
                keyboardController?.hide()
            }
        ) {
            Icon(
                painter = when {
                    viewModel.state.isRunning -> runningIcon()
                    else -> viewModel.waitIcon()
                },
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )

            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))

            Text(
                text = when {
                    viewModel.state.isRunning -> viewModel.runningString()
                    else -> viewModel.waitString()
                }
            )
        }
    }
}

@Composable
private fun CryptoViewModel.waitIcon() =
    painterResource(
        id = when {
            isSkip -> R.drawable.lock_off
            isEncrypt -> R.drawable.lock
            isDecrypt -> R.drawable.lock_open
            else -> R.drawable.lock_off
        }
    )

@Composable
private fun runningIcon() =
    painterResource(
        id = R.drawable.lock
    )

@Composable
private fun CryptoViewModel.waitString() =
    stringResource(
        id = when {
            isSkip -> R.string.crypto_skip
            isEncrypt -> R.string.crypto_encrypt
            isDecrypt -> R.string.crypto_decrypt
            else -> R.string.crypto_skip
        }
    )

@Composable
private fun CryptoViewModel.runningString() =
    stringResource(
        id = when {
            isEncrypt -> R.string.crypto_encrypting
            isDecrypt -> R.string.crypto_decrypting
            else -> R.string.crypto_skip
        }
    )