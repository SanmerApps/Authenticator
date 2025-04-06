package dev.sanmer.crypto

import dev.sanmer.crypto.Crypto.Default.generateSecretKey
import dev.sanmer.crypto.Crypto.Default.randomIv
import dev.sanmer.crypto.Crypto.Default.randomSalt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class PasswordKey private constructor(
    private val password: CharArray
) : Crypto {
    private val salt by lazy { randomSalt }

    private val keys = hashMapOf<Int, SecretKey>()
    private fun generateSecretKey(salt: ByteArray): SecretKey {
        return keys.getOrPut(salt.contentHashCode()) {
            password.generateSecretKey(salt)
        }
    }

    override suspend fun encrypt(input: ByteArray) = withContext(Dispatchers.IO) {
        val iv = randomIv
        val key = generateSecretKey(salt)
        val cipher = Cipher.getInstance(Crypto.ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(Crypto.TAG_LENGTH, iv))
        salt + iv + cipher.doFinal(input)
    }

    override suspend fun decrypt(input: ByteArray) = withContext(Dispatchers.IO) {
        val salt = input.copyOfRange(0, Crypto.SALT_LENGTH)
        val iv = input.copyOfRange(Crypto.SALT_LENGTH, Crypto.SALT_LENGTH + Crypto.IV_LENGTH)
        val data = input.copyOfRange(Crypto.SALT_LENGTH + Crypto.IV_LENGTH, input.size)

        val key = generateSecretKey(salt)
        val cipher = Cipher.getInstance(Crypto.ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(Crypto.TAG_LENGTH, iv))
        cipher.doFinal(data)
    }

    companion object Default {
        fun new(password: String) = PasswordKey(password.toCharArray())
    }
}
