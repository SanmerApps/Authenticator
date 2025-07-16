package dev.sanmer.authenticator.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import dev.sanmer.authenticator.ui.screens.crypto.CryptoScreen
import dev.sanmer.authenticator.ui.screens.crypto.CryptoViewModel
import dev.sanmer.authenticator.ui.theme.AppTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.UUID

class CryptoActivity : ComponentActivity() {
    private val viewModel by viewModel<CryptoViewModel>()

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
            putExtra(EXTRA_OUTPUT, viewModel.output.toTypedArray())
            setResult(RESULT_OK, this)
        }
        super.finish()
    }

    enum class Action(
        val original: String,
    ) {
        Encrypt(ACTION_ENCRYPT),
        Decrypt(ACTION_DECRYPT);

        companion object Default {
            operator fun invoke(action: String?): Action {
                return entries.first { it.original == action }
            }
        }
    }

    companion object Default {
        private const val ACTION_ENCRYPT = "dev.sanmer.authenticator.action.ENCRYPT"
        private const val ACTION_DECRYPT = "dev.sanmer.authenticator.action.DECRYPT"
        const val EXTRA_BYPASS = "dev.sanmer.authenticator.extra.BYPASS"
        const val EXTRA_INPUT = "dev.sanmer.authenticator.extra.INPUT"
        const val EXTRA_OUTPUT = "dev.sanmer.authenticator.extra.OUTPUT"

        val Intent.bypass: Boolean
            inline get() = getBooleanExtra(EXTRA_BYPASS, true)

        val Intent.input: Array<String>
            inline get() = checkNotNull(getStringArrayExtra(EXTRA_INPUT))

        val Intent.output: Array<String>
            inline get() = checkNotNull(getStringArrayExtra(EXTRA_OUTPUT))

        private fun start(
            context: Context,
            input: List<String>,
            action: Action,
            bypass: Boolean,
            callback: (List<String>) -> Unit
        ) {
            if (context !is ActivityResultRegistryOwner) return

            val activityResultRegistry = context.activityResultRegistry
            val launcher = activityResultRegistry.register(
                key = UUID.randomUUID().toString(),
                contract = Crypto(action, bypass),
                callback = { if (it.isNotEmpty()) callback(it) }
            )

            launcher.launch(input)
        }

        fun encrypt(
            context: Context,
            input: List<String>,
            bypass: Boolean = true,
            callback: (List<String>) -> Unit
        ) = start(
            context = context,
            input = input,
            action = Action.Encrypt,
            bypass = bypass,
            callback = callback
        )

        fun decrypt(
            context: Context,
            input: List<String>,
            bypass: Boolean = true,
            callback: (List<String>) -> Unit
        ) = start(
            context = context,
            input = input,
            action = Action.Decrypt,
            bypass = bypass,
            callback = callback
        )
    }

    private class Crypto(
        private val action: Action,
        private val bypass: Boolean
    ) : ActivityResultContract<List<String>, List<String>>() {
        override fun createIntent(context: Context, input: List<String>): Intent {
            return Intent(context, CryptoActivity::class.java).also {
                it.action = action.original
                it.putExtra(EXTRA_BYPASS, bypass)
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