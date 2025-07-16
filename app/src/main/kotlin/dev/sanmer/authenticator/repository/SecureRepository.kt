package dev.sanmer.authenticator.repository

import dev.sanmer.crypto.Crypto
import dev.sanmer.crypto.SessionKey

interface SecureRepository {
    fun setSessionKey(sessionKey: SessionKey?)
    suspend fun encrypt(input: String): String
    suspend fun decrypt(input: String): String
    suspend fun encryptSecret(key: Crypto)
    suspend fun decryptSecret(key: Crypto)
    suspend fun encryptSecretByNewKey(current: Crypto, new: Crypto)
}