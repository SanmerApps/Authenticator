package dev.sanmer.crypto

import dev.sanmer.crypto.Crypto.Default.randomIv
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class SessionKey(
    val key: SecretKey
) : Crypto {
    override suspend fun encrypt(input: ByteArray) = withContext(Dispatchers.IO) {
        val iv = randomIv
        val cipher = Cipher.getInstance(Crypto.ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(Crypto.TAG_LENGTH, iv))
        iv + cipher.doFinal(input)
    }

    override suspend fun decrypt(input: ByteArray) = withContext(Dispatchers.IO) {
        val iv = input.copyOfRange(0, Crypto.IV_LENGTH)
        val data = input.copyOfRange(Crypto.IV_LENGTH, input.size)

        val cipher = Cipher.getInstance(Crypto.ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(Crypto.TAG_LENGTH, iv))
        cipher.doFinal(data)
    }

    suspend fun getKeyEncryptedByPassword(password: String) =
        PasswordKey.new(password).encrypt(key.encoded)

    companion object Default {
        fun ByteArray.toSecretKey() =
            SecretKeySpec(this, 0, size, Crypto.KEY_ALGORITHM)

        fun new() = SessionKey(
            key = KeyGenerator.getInstance(Crypto.KEY_ALGORITHM).apply {
                init(Crypto.KEY_LENGTH)
            }.generateKey()
        )

        suspend fun decryptKeyByPassword(key: ByteArray, password: String) = SessionKey(
            key = PasswordKey.new(password).decrypt(key).toSecretKey()
        )
    }
}