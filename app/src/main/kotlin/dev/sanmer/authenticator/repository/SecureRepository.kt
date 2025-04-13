package dev.sanmer.authenticator.repository

import dev.sanmer.authenticator.database.dao.TotpDao
import dev.sanmer.crypto.Crypto
import dev.sanmer.crypto.SessionKey
import dev.sanmer.encoding.decodeBase64
import dev.sanmer.encoding.encodeBase64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureRepository @Inject constructor(
    private val totp: TotpDao,
) {
    private var key: Crypto? = null

    fun setSessionKey(sessionKey: SessionKey?) {
        key = sessionKey
    }

    suspend fun encrypt(input: String) = key?.encrypt(input) ?: input

    suspend fun decrypt(input: String) = key?.decrypt(input) ?: input

    suspend fun encryptSecret(key: Crypto) = withContext(Dispatchers.IO) {
        totp.update(
            totp.getAll().map { it.copy(secret = key.encrypt(it.secret)) }
        )
    }

    suspend fun decryptSecret(key: Crypto) = withContext(Dispatchers.IO) {
        totp.update(
            totp.getAll().map { it.copy(secret = key.decrypt(it.secret)) }
        )
    }

    suspend fun encryptSecretByNewKey(current: Crypto, new: Crypto) = withContext(Dispatchers.IO) {
        totp.update(
            totp.getAll().map {
                val decrypted = current.decrypt(it.secret.decodeBase64())
                it.copy(secret = new.encrypt(decrypted).encodeBase64())
            }
        )
    }
}