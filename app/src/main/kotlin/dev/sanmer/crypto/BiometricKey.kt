package dev.sanmer.crypto

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.biometric.AuthenticationRequest.Biometric
import androidx.biometric.AuthenticationResult
import androidx.biometric.AuthenticationResultLauncher
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators
import androidx.biometric.BiometricPrompt
import androidx.biometric.registerForAuthenticationResult
import androidx.fragment.app.FragmentActivity
import dev.sanmer.authenticator.BuildConfig
import dev.sanmer.authenticator.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withContext
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class BiometricKey(context: Context) : Crypto {
    private val key by lazy { getSecretKey() }

    private val builder by lazy {
        Biometric.Builder(
            title = context.getString(R.string.biometric_title),
            authFallback = Biometric.Fallback.NegativeButton(
                context.getString(R.string.biometric_cancel)
            )
        ).apply {
            setIsConfirmationRequired(true)
        }
    }

    private suspend fun Cipher.authenticated(): Cipher {
        val launcher = checkNotNull(launcher) { "BiometricKey uninitialized" }
        val biometric = builder.setMinStrength(
            Biometric.Strength.Class3(BiometricPrompt.CryptoObject(this@authenticated))
        ).build()

        launcher.launch(biometric)
        return when (val result = channel.receive()) {
            is AuthenticationResult.Error -> throw IllegalStateException(result.errString.toString())
            is AuthenticationResult.Success -> requireNotNull(result.crypto?.cipher) { "Expect cipher" }
        }
    }

    override suspend fun encrypt(input: ByteArray): ByteArray = withContext(Dispatchers.IO) {
        val cipher = Cipher.getInstance(Crypto.ALGORITHM).let {
            it.init(Cipher.ENCRYPT_MODE, key)
            it.authenticated()
        }
        cipher.iv + cipher.doFinal(input)
    }

    override suspend fun decrypt(input: ByteArray): ByteArray = withContext(Dispatchers.IO) {
        val iv = input.copyOfRange(0, Crypto.IV_LENGTH)
        val data = input.copyOfRange(Crypto.IV_LENGTH, input.size)

        val cipher = Cipher.getInstance(Crypto.ALGORITHM).let {
            it.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(Crypto.TAG_LENGTH, iv))
            it.authenticated()
        }
        cipher.doFinal(data)
    }

    companion object Default {
        fun canAuthenticate(context: Context) = BiometricManager.from(context)
            .canAuthenticate(Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS

        private var launcher: AuthenticationResultLauncher? = null
        private val channel = Channel<AuthenticationResult>()
        fun init(activity: FragmentActivity) {
            launcher = activity.registerForAuthenticationResult { result ->
                channel.trySend(result)
            }
        }

        private fun generateSecretKey(keyGenParameterSpec: KeyGenParameterSpec) =
            KeyGenerator.getInstance(Crypto.KEY_ALGORITHM, "AndroidKeyStore")
                .apply { init(keyGenParameterSpec) }
                .generateKey()

        private fun getSecretKey() = KeyStore.getInstance("AndroidKeyStore")
            .apply { load(null) }
            .getKey(BuildConfig.APPLICATION_ID, null)
            .let { requireNotNull(it) { "Expect key(alias=${BuildConfig.APPLICATION_ID})" } }

        fun new(): SecretKey = generateSecretKey(
            KeyGenParameterSpec.Builder(
                BuildConfig.APPLICATION_ID,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setUserAuthenticationRequired(true)
                .setUserAuthenticationParameters(0, KeyProperties.AUTH_BIOMETRIC_STRONG)
                .setInvalidatedByBiometricEnrollment(true)
                .build()
        )

        suspend fun SessionKey.getKeyEncryptedByBiometric(context: Context) =
            BiometricKey(context).encrypt(key.encoded)

        suspend fun SessionKey.Default.decryptKeyByBiometric(
            key: ByteArray,
            context: Context
        ) = SessionKey(
            key = BiometricKey(context).decrypt(key).toSecretKey()
        )

    }
}