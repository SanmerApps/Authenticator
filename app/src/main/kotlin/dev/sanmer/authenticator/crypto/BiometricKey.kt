package dev.sanmer.authenticator.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.activity.ComponentActivity
import androidx.biometric.AuthenticationRequest.Biometric
import androidx.biometric.AuthenticationResult
import androidx.biometric.AuthenticationResultLauncher
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators
import androidx.biometric.BiometricPrompt
import androidx.biometric.registerForAuthenticationResult
import dev.sanmer.auth.crypto.Crypto
import dev.sanmer.auth.crypto.SessionKey
import dev.sanmer.authenticator.BuildConfig
import dev.sanmer.authenticator.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withContext
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec

class BiometricKey private constructor(
    val key: Key
) : Crypto {
    constructor() : this(getSecretKey())

    private suspend fun Cipher.authenticated() = withContext(Dispatchers.Main) {
        val launcher = checkNotNull(launcher) { "BiometricKey uninitialized" }
        val builder = checkNotNull(builder) { "BiometricKey uninitialized" }

        val biometric = builder.setMinStrength(
            Biometric.Strength.Class3(BiometricPrompt.CryptoObject(this@authenticated))
        ).build()

        launcher.launch(biometric)
        when (val result = channel.receive()) {
            is AuthenticationResult.Success -> checkNotNull(result.crypto?.cipher) { "Expect cipher" }
            is AuthenticationResult.Error -> throw IllegalStateException(result.errString.toString())
            is AuthenticationResult.CustomFallbackSelected -> throw IllegalStateException("CustomFallbackSelected")
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
        private var launcher: AuthenticationResultLauncher? = null
        private var builder: Biometric.Builder? = null
        private val channel = Channel<AuthenticationResult>()

        val isInitialized get() = launcher != null && builder != null

        fun init(activity: ComponentActivity) {
            if (BiometricManager.from(activity)
                    .canAuthenticate(Authenticators.BIOMETRIC_STRONG) != BiometricManager.BIOMETRIC_SUCCESS
            ) {
                return
            }
            launcher = activity.registerForAuthenticationResult { result ->
                channel.trySend(result)
            }
            builder = Biometric.Builder(
                title = activity.getString(R.string.setting_biometric_verify)
            ).apply {
                setIsConfirmationRequired(true)
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

        fun new() = generateSecretKey(
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
        ).let(::BiometricKey)

        suspend fun SessionKey.getKeyEncryptedByBiometric() =
            new().encrypt(key.encoded)

        suspend fun SessionKey.Default.decryptKeyByBiometric(
            key: ByteArray
        ) = SessionKey(
            key = BiometricKey().decrypt(key).toSecretKey()
        )

    }
}