package dev.sanmer.authenticator.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.FragmentActivity
import dev.sanmer.authenticator.ui.screens.authorize.AuthorizeScreen
import dev.sanmer.authenticator.ui.screens.authorize.AuthorizeViewModel
import dev.sanmer.authenticator.ui.theme.AppTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.UUID

class AuthorizeActivity : FragmentActivity() {
    private val viewModel by viewModel<AuthorizeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        viewModel.updateFromIntent(::getIntent)

        setContent {
            AppTheme {
                AuthorizeScreen()
            }
        }
    }

    override fun finish() {
        Intent().apply {
            putExtra(EXTRA_SUCCEED, viewModel.type.isSucceed)
            setResult(RESULT_OK, this)
        }
        super.finish()
    }

    enum class Action(
        val original: String
    ) {
        SetupPassword(ACTION_SETUP_PASSWORD),
        ChangePassword(ACTION_CHANGE_PASSWORD),
        RemovePassword(ACTION_REMOVE_PASSWORD),
        SetupBiometric(ACTION_SETUP_BIOMETRIC),
        RemoveBiometric(ACTION_REMOVE_BIOMETRIC),
        Auth(ACTION_AUTH);

        companion object Default {
            operator fun invoke(action: String?): Action {
                return entries.first { it.original == action }
            }
        }
    }

    companion object Default {
        private const val ACTION_SETUP_PASSWORD = "dev.sanmer.authenticator.action.SETUP_PASSWORD"
        private const val ACTION_CHANGE_PASSWORD = "dev.sanmer.authenticator.action.CHANGE_PASSWORD"
        private const val ACTION_REMOVE_PASSWORD = "dev.sanmer.authenticator.action.REMOVE_PASSWORD"
        private const val ACTION_SETUP_BIOMETRIC = "dev.sanmer.authenticator.action.SETUP_BIOMETRIC"
        private const val ACTION_REMOVE_BIOMETRIC =
            "dev.sanmer.authenticator.action.REMOVE_BIOMETRIC"
        private const val ACTION_AUTH = "dev.sanmer.authenticator.action.AUTH"
        const val EXTRA_SUCCEED = "dev.sanmer.authenticator.extra.SUCCEED"

        val Intent.isSucceed: Boolean
            inline get() = getBooleanExtra(EXTRA_SUCCEED, false)

        fun start(
            context: Context,
            action: Action,
            callback: (Boolean) -> Unit
        ) {
            if (context !is ActivityResultRegistryOwner) return

            val activityResultRegistry = context.activityResultRegistry
            val launcher = activityResultRegistry.register(
                key = UUID.randomUUID().toString(),
                contract = Authorize(action),
                callback = callback
            )

            launcher.launch(Unit)
        }

        fun auth(
            context: Context,
            callback: (Boolean) -> Unit
        ) = start(
            context = context,
            action = Action.Auth,
            callback = callback
        )

        private class Authorize(
            val action: Action
        ) : ActivityResultContract<Unit, Boolean>() {
            override fun createIntent(context: Context, input: Unit): Intent {
                return Intent(context, AuthorizeActivity::class.java).also {
                    it.action = action.original
                }
            }

            override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
                if (resultCode != RESULT_OK) return false
                if (intent == null) return false
                return intent.isSucceed
            }

        }
    }
}