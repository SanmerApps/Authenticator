package dev.sanmer.authenticator.ui.screens.authorize

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import dev.sanmer.authenticator.ktx.finishActivity
import dev.sanmer.authenticator.ui.AuthorizeActivity.Action
import dev.sanmer.authenticator.ui.screens.authorize.component.ChangePasswordItem
import dev.sanmer.authenticator.ui.screens.authorize.component.EnterPasswordItem
import dev.sanmer.authenticator.viewmodel.AuthorizeViewModel

@Composable
fun AuthorizeScreen(
    viewModel: AuthorizeViewModel = hiltViewModel()
) {
    if (!viewModel.loadState.isReady) return
    val context = LocalContext.current
    val activity = remember { context as FragmentActivity }

    DisposableEffect(viewModel.type) {
        if (viewModel.type.isBiometricPending) {
            viewModel.loadSessionKeyByBiometric(activity)
        }
        if (viewModel.type.isSucceed) {
            context.finishActivity()
        }
        onDispose {}
    }

    when (viewModel.action) {
        Action.ChangePassword -> ChangePasswordItem(
            onDismiss = context::finishActivity,
            isPasswordError = viewModel.type.isPasswordFailed,
            onChange = viewModel::changePassword
        )
        Action.Auth -> if (viewModel.type.isPassword) EnterPasswordItem(
            onDismiss = context::finishActivity,
            action = viewModel.action,
            isPasswordError = viewModel.type.isPasswordFailed,
            onEnter = viewModel::loadSessionKeyByPassword,
            enableBiometric = viewModel.isSupportedBiometric,
            onBiometric = viewModel::retryBiometric
        )
        Action.SetupPassword -> EnterPasswordItem(
            onDismiss = context::finishActivity,
            action = viewModel.action,
            isPasswordError = viewModel.type.isPasswordFailed,
            onEnter = viewModel::setupPassword
        )
        Action.RemovePassword -> EnterPasswordItem(
            onDismiss = context::finishActivity,
            action = viewModel.action,
            isPasswordError = viewModel.type.isPasswordFailed,
            onEnter = viewModel::removePassword
        )
        Action.SetupBiometric -> EnterPasswordItem(
            onDismiss = context::finishActivity,
            action = viewModel.action,
            isPasswordError = viewModel.type.isPasswordFailed,
            onEnter = { viewModel.setupBiometric(it, activity) }
        )
        Action.RemoveBiometric -> EnterPasswordItem(
            onDismiss = context::finishActivity,
            action = viewModel.action,
            isPasswordError = viewModel.type.isPasswordFailed,
            onEnter = viewModel::removeBiometric
        )
    }
}