package dev.sanmer.crypto

import dev.sanmer.encoding.decodeBase64
import dev.sanmer.encoding.encodeBase64
import java.security.SecureRandom
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

interface Crypto {
    suspend fun encrypt(input: ByteArray): ByteArray
    suspend fun encrypt(input: String) = encrypt(input.toByteArray(Charsets.UTF_8)).encodeBase64()

    suspend fun decrypt(input: ByteArray): ByteArray
    suspend fun decrypt(input: String) = decrypt(input.decodeBase64()).toString(Charsets.UTF_8)

    companion object Default {
        const val FACTORY_ALGORITHM = "PBKDF2WithHmacSHA256"
        const val ALGORITHM = "AES/GCM/NoPadding"
        const val KEY_ALGORITHM = "AES"
        const val ITERATION_COUNT = 102400
        const val KEY_LENGTH = 256
        const val SALT_LENGTH = 16
        const val IV_LENGTH = 12
        const val TAG_LENGTH = 128

        val randomSalt: ByteArray
            inline get() = ByteArray(SALT_LENGTH).apply {
                SecureRandom().nextBytes(this)
            }

        val randomIv: ByteArray
            inline get() = ByteArray(IV_LENGTH).apply {
                SecureRandom().nextBytes(this)
            }

        fun CharArray.generateSecretKey(salt: ByteArray): SecretKey {
            val factory = SecretKeyFactory.getInstance(FACTORY_ALGORITHM)
            val spec = PBEKeySpec(this, salt, ITERATION_COUNT, KEY_LENGTH)
            val secret = factory.generateSecret(spec)
            return SecretKeySpec(secret.encoded, KEY_ALGORITHM)
        }
    }
}