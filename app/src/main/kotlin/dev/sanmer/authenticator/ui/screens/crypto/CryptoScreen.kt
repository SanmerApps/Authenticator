package dev.sanmer.authenticator.ui.screens.crypto

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ktx.finishActivity
import dev.sanmer.authenticator.ui.screens.authorize.component.PasswordTextField
import dev.sanmer.authenticator.ui.screens.settings.component.SettingBottomSheet
import org.koin.androidx.compose.koinViewModel

@Composable
fun CryptoScreen(
    viewModel: CryptoViewModel = koinViewModel()
) {
    val context = LocalContext.current

    DisposableEffect(viewModel.state) {
        if (viewModel.state.isSucceed) context.finishActivity()
        onDispose {}
    }

    SettingBottomSheet(
        onDismiss = context::finishActivity,
        title = stringResource(
            id = if (viewModel.isEncrypt) {
                R.string.crypto_encrypt
            } else {
                R.string.crypto_decrypt
            }
        )
    ) {
        PasswordTextField(
            password = viewModel.password,
            onPasswordChange = viewModel::updatePassword,
            onImeDone = viewModel::crypto,
            isError = viewModel.state.isFailed,
            title = stringResource(id = R.string.security_password),
            modifier = Modifier.fillMaxWidth(),
            actionIcon = if (viewModel.isSkip) R.drawable.lock_off else null,
            onActionClick = if (viewModel.isSkip) viewModel::crypto else null
        )
    }
}