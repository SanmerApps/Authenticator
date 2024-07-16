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
import dev.sanmer.authenticator.ui.main.CryptoScreen
import dev.sanmer.authenticator.ui.theme.AppTheme
import dev.sanmer.authenticator.viewmodel.CryptoViewModel
import java.util.UUID

@AndroidEntryPoint
class CryptoActivity : ComponentActivity() {
    private val viewModel: CryptoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        viewModel.updateFromIntent(::getIntent)

        setContent {
            AppTheme {
                CryptoScreen()
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

        companion object {
            fun fromStr(action: String?): Action? {
                return when (action) {
                    ACTION_ENCRYPT -> Encrypt
                    ACTION_DECRYPT -> Decrypt
                    else -> null
                }
            }
        }
    }

    companion object {
        const val ACTION_ENCRYPT = "dev.sanmer.authenticator.action.ENCRYPT"
        const val ACTION_DECRYPT = "dev.sanmer.authenticator.action.DECRYPT"
        const val EXTRA_INPUT = "dev.sanmer.authenticator.extra.INPUT"
        const val EXTRA_OUTPUT = "dev.sanmer.authenticator.extra.OUTPUT"

        val Intent.input: Array<String>
            get() = checkNotNull(getStringArrayExtra(EXTRA_INPUT))

        val Intent.output: Array<String>
            get() = checkNotNull(getStringArrayExtra(EXTRA_OUTPUT))

        val Encrypt: ActivityResultContract<List<String>, List<String>> = Crypto(Action.Encrypt)
        val Decrypt: ActivityResultContract<List<String>, List<String>> = Crypto(Action.Decrypt)

        fun encrypt(context: Context, input: List<String>, callback: (List<String>) -> Unit) {
            if (context !is ActivityResultRegistryOwner) return

            val activityResultRegistry = context.activityResultRegistry
            val launcher = activityResultRegistry.register(
                key = UUID.randomUUID().toString(),
                contract = Encrypt,
                callback = {
                    if (it.isNotEmpty()) callback(it)
                }
            )

            launcher.launch(input)
        }

        fun decrypt(context: Context, input: List<String>, callback: (List<String>) -> Unit) {
            if (context !is ActivityResultRegistryOwner) return

            val activityResultRegistry = context.activityResultRegistry
            val launcher = activityResultRegistry.register(
                key = UUID.randomUUID().toString(),
                contract = Decrypt,
                callback = {
                    if (it.isNotEmpty()) callback(it)
                }
            )

            launcher.launch(input)
        }
    }

    private class Crypto(private val action: Action) :
        ActivityResultContract<List<String>, List<String>>() {
        override fun createIntent(context: Context, input: List<String>): Intent {
            return when (action) {
                Action.Encrypt -> Intent(ACTION_ENCRYPT)
                Action.Decrypt -> Intent(ACTION_DECRYPT)
            }.putExtra(
                EXTRA_INPUT, input.toTypedArray()
            )
        }

        override fun parseResult(resultCode: Int, intent: Intent?): List<String> {
            if (resultCode != RESULT_OK) return emptyList()
            if (intent == null) return emptyList()
            return intent.output.toList()
        }
    }
}