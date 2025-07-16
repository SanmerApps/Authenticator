package dev.sanmer.authenticator.repository

import dev.sanmer.authenticator.database.dao.TotpDao
import dev.sanmer.crypto.Crypto
import dev.sanmer.crypto.SessionKey
import dev.sanmer.encoding.decodeBase64
import dev.sanmer.encoding.encodeBase64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SecureRepositoryImpl(
    private val totp: TotpDao
) : SecureRepository {
    private var key: Crypto? = null

    override fun setSessionKey(sessionKey: SessionKey?) {
        key = sessionKey
    }

    override suspend fun encrypt(input: String) = key?.encrypt(input) ?: input

    override suspend fun decrypt(input: String) = key?.decrypt(input) ?: input

    override suspend fun encryptSecret(key: Crypto) = withContext(Dispatchers.IO) {
        totp.update(
            totp.getAll().map { it.copy(secret = key.encrypt(it.secret)) }
        )
    }

    override suspend fun decryptSecret(key: Crypto) = withContext(Dispatchers.IO) {
        totp.update(
            totp.getAll().map { it.copy(secret = key.decrypt(it.secret)) }
        )
    }

    override suspend fun encryptSecretByNewKey(current: Crypto, new: Crypto) =
        withContext(Dispatchers.IO) {
            totp.update(
                totp.getAll().map {
                    val decrypted = current.decrypt(it.secret.decodeBase64())
                    it.copy(secret = new.encrypt(decrypted).encodeBase64())
                }
            )
        }
}