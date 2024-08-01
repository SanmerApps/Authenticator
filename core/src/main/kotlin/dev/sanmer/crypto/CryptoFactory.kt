package dev.sanmer.crypto

import dev.sanmer.encoding.decodeBase64
import dev.sanmer.encoding.encodeBase64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class CryptoFactory(
    private val password: CharArray
) {
    constructor(password: String) : this(
        password = password.toCharArray()
    )

    private val salt by lazy { randomSalt }
    private val secrets = hashMapOf<String, SecretKey>()

    private fun generateSecret(salt: ByteArray): SecretKey {
        return secrets.getOrPut(salt.encodeBase64()) {
            password.generateSecret(salt)
        }
    }

    fun encrypt(input: ByteArray): ByteArray {
        val iv = randomIv
        val key = generateSecret(salt)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(iv))
        return salt + iv + cipher.doFinal(input)
    }

    fun encrypt(input: String) =
        encrypt(input.toByteArray(Charsets.UTF_8)).encodeBase64()

    fun decrypt(input: ByteArray): ByteArray {
        val salt = input.copyOfRange(0, SALT_LENGTH)
        val iv = input.copyOfRange(SALT_LENGTH, SALT_LENGTH + IV_LENGTH)
        val data = input.copyOfRange(SALT_LENGTH + IV_LENGTH, input.size)

        val key = generateSecret(salt)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
        return cipher.doFinal(data)
    }

    fun decrypt(input: String) =
        decrypt(input.decodeBase64()).toString(Charsets.UTF_8)

    internal companion object Default {
        const val FACTORY_ALGORITHM = "PBKDF2WithHmacSHA256"
        const val ALGORITHM = "AES/CBC/PKCS5Padding"
        const val KEY_ALGORITHM = "AES"
        const val ITERATION_COUNT = 102400
        const val KEY_LENGTH = 128
        const val SALT_LENGTH = 16
        const val IV_LENGTH = 16

        val randomSalt: ByteArray
            inline get() = ByteArray(SALT_LENGTH).apply {
                SecureRandom().nextBytes(this)
            }

        val randomIv: ByteArray
            inline get() = ByteArray(IV_LENGTH).apply {
                SecureRandom().nextBytes(this)
            }

        fun CharArray.generateSecret(salt: ByteArray): SecretKey {
            val factory = SecretKeyFactory.getInstance(FACTORY_ALGORITHM)
            val spec = PBEKeySpec(this, salt, ITERATION_COUNT, KEY_LENGTH)
            val secret = factory.generateSecret(spec)
            return SecretKeySpec(secret.encoded, KEY_ALGORITHM)
        }
    }
}