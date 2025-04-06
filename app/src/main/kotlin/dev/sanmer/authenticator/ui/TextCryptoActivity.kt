package dev.sanmer.authenticator.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.sanmer.authenticator.BuildConfig
import dev.sanmer.authenticator.ui.main.TextCryptoScreen
import dev.sanmer.authenticator.ui.theme.AppTheme
import dev.sanmer.authenticator.viewmodel.TextCryptoViewModel
import java.util.UUID

@AndroidEntryPoint
class TextCryptoActivity : ComponentActivity() {
    private val viewModel: TextCryptoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        viewModel.updateFromIntent(::getIntent)

        setContent {
            AppTheme {
                TextCryptoScreen()
            }
        }
    }

    override fun finish() {
        Intent().apply {
            putExtra(EXTRA_OUTPUT, viewModel.data.toTypedArray())
            setResult(RESULT_OK, this)
        }
        super.finish()
    }

    enum class Action {
        Encrypt,
        Decrypt;

        companion object Default {
            operator fun invoke(action: String?): Action {
                return when (action) {
                    ACTION_ENCRYPT -> Encrypt
                    ACTION_DECRYPT -> Decrypt
                    else -> throw IllegalArgumentException("Unsupported($action)")
                }
            }
        }
    }

    companion object Default {
        private const val ACTION_ENCRYPT = "${BuildConfig.APPLICATION_ID}.action.ENCRYPT"
        private const val ACTION_DECRYPT = "${BuildConfig.APPLICATION_ID}.action.DECRYPT"
        const val EXTRA_ALLOW_SKIP = "${BuildConfig.APPLICATION_ID}.extra.ALLOW_SKIP"
        const val EXTRA_INPUT = "${BuildConfig.APPLICATION_ID}.extra.INPUT"
        const val EXTRA_OUTPUT = "${BuildConfig.APPLICATION_ID}.extra.OUTPUT"

        val Intent.allowSkip: Boolean
            inline get() = getBooleanExtra(EXTRA_ALLOW_SKIP, true)

        val Intent.input: Array<String>
            inline get() = checkNotNull(getStringArrayExtra(EXTRA_INPUT))

        val Intent.output: Array<String>
            inline get() = checkNotNull(getStringArrayExtra(EXTRA_OUTPUT))

        fun encrypt(
            context: Context,
            input: List<String>,
            allowSkip: Boolean = true,
            callback: (List<String>) -> Unit
        ) {
            if (context !is ActivityResultRegistryOwner) return

            val activityResultRegistry = context.activityResultRegistry
            val launcher = activityResultRegistry.register(
                key = UUID.randomUUID().toString(),
                contract = Crypto(Action.Encrypt, allowSkip),
                callback = {
                    if (it.isNotEmpty()) callback(it)
                }
            )

            launcher.launch(input)
        }

        fun decrypt(
            context: Context,
            input: List<String>,
            allowSkip: Boolean = true,
            callback: (List<String>) -> Unit
        ) {
            if (context !is ActivityResultRegistryOwner) return

            val activityResultRegistry = context.activityResultRegistry
            val launcher = activityResultRegistry.register(
                key = UUID.randomUUID().toString(),
                contract = Crypto(Action.Decrypt, allowSkip),
                callback = {
                    if (it.isNotEmpty()) callback(it)
                }
            )

            launcher.launch(input)
        }
    }

    private class Crypto(
        private val action: Action,
        private val allowSkip: Boolean
    ) : ActivityResultContract<List<String>, List<String>>() {
        override fun createIntent(context: Context, input: List<String>): Intent {
            return when (action) {
                Action.Encrypt -> Intent(ACTION_ENCRYPT)
                Action.Decrypt -> Intent(ACTION_DECRYPT)
            }.also {
                it.putExtra(EXTRA_ALLOW_SKIP, allowSkip)
                it.putExtra(EXTRA_INPUT, input.toTypedArray())
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): List<String> {
            if (resultCode != RESULT_OK) return emptyList()
            if (intent == null) return emptyList()
            return intent.output.toList()
        }
    }
}