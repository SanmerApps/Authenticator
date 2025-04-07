package dev.sanmer.crypto

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.biometric.AuthenticationRequest.Biometric
import androidx.biometric.AuthenticationResult
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators
import androidx.biometric.BiometricPrompt
import androidx.biometric.registerForAuthenticationResult
import androidx.fragment.app.FragmentActivity
import dev.sanmer.authenticator.BuildConfig
import dev.sanmer.authenticator.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class BiometricKey(
    private val activity: FragmentActivity
) : Crypto {
    private val key by lazy { getSecretKey() }

    private val biometric by lazy {
        Biometric.Builder(
            title = activity.getString(R.string.biometric_title),
            authFallback = Biometric.Fallback.NegativeButton(
                activity.getString(R.string.biometric_cancel)
            )
        ).apply {
            setSubtitle(activity.getString(R.string.biometric_desc))
            setIsConfirmationRequired(true)
        }
    }

    private suspend fun Biometric.authenticate(
        activity: FragmentActivity
    ) = withContext(Dispatchers.Main) {
        suspendCancellableCoroutine { continuation ->
            val request = activity.registerForAuthenticationResult { result ->
                when (result) {
                    is AuthenticationResult.Error -> {
                        Timber.w("onAuthenticationError(${result.errorCode}): ${result.errString}")
                        continuation.resumeWithException(IllegalStateException("${result.errString}"))
                    }

                    is AuthenticationResult.Success -> {
                        Timber.d("onAuthenticationSucceeded: ${result.authType}")
                        continuation.resume(result)
                    }
                }
            }

            request.launch(this@authenticate)
            continuation.invokeOnCancellation {
                request.cancel()
            }
        }
    }

    private suspend fun <T> Cipher.authenticate(block: (Cipher) -> T): T {
        val result = biometric.apply {
            setMinStrength(
                Biometric.Strength.Class3(BiometricPrompt.CryptoObject(this@authenticate))
            )
        }.build().authenticate(activity)
        return block(requireNotNull(result.crypto?.cipher) { "Expect Cipher" })
    }

    override suspend fun encrypt(input: ByteArray) = withContext(Dispatchers.IO) {
        val cipher = Cipher.getInstance(Crypto.ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        cipher.authenticate { it.iv + it.doFinal(input)!! }
    }

    override suspend fun decrypt(input: ByteArray) = withContext(Dispatchers.IO) {
        val iv = input.copyOfRange(0, Crypto.IV_LENGTH)
        val data = input.copyOfRange(Crypto.IV_LENGTH, input.size)

        val cipher = Cipher.getInstance(Crypto.ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(Crypto.TAG_LENGTH, iv))
        cipher.authenticate { it.doFinal(data)!! }
    }

    companion object Default {
        fun canAuthenticate(context: Context) = BiometricManager.from(context)
            .canAuthenticate(Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS

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

        suspend fun SessionKey.getKeyEncryptedByBiometric(activity: FragmentActivity) =
            BiometricKey(activity).encrypt(key.encoded)

        suspend fun SessionKey.Default.decryptKeyByBiometric(key: ByteArray, activity: FragmentActivity) =
            SessionKey(
                key = BiometricKey(activity).decrypt(key).toSecretKey()
            )

    }
}