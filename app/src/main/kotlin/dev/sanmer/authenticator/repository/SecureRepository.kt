package dev.sanmer.authenticator.repository

import dev.sanmer.authenticator.database.dao.HotpDao
import dev.sanmer.authenticator.database.dao.TotpDao
import dev.sanmer.authenticator.database.dao.TrashDao
import dev.sanmer.authenticator.ktx.HashBiMap
import dev.sanmer.crypto.Crypto
import dev.sanmer.crypto.SessionKey
import dev.sanmer.encoding.decodeBase64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureRepository @Inject constructor(
    private val trash: TrashDao,
    private val hotp: HotpDao,
    private val totp: TotpDao,
) {
    private var key: Crypto? = null

    private val secrets = HashBiMap<String, String>()

    suspend fun encrypt(input: String) =
        secrets.getKey(input) ?: key?.encrypt(input) ?: input

    suspend fun decrypt(input: String) =
        secrets.get(input) ?: key?.decrypt(input)?.also {
            secrets.put(input, it)
        } ?: input

    fun setSessionKey(sessionKey: SessionKey?) {
        key = sessionKey
    }

    suspend fun encryptSecret(key: Crypto) = withContext(Dispatchers.IO) {
        val trashEncrypted = trash.getAll().map { it.copy(secret = key.encrypt(it.secret)) }
        trash.deleteAll()
        trash.insert(trashEncrypted)

        val hotpEncrypted = hotp.getAll().map { it.copy(secret = key.encrypt(it.secret)) }
        hotp.deleteAll()
        hotp.insert(hotpEncrypted)

        val totpEncrypted = totp.getAll().map { it.copy(secret = key.encrypt(it.secret)) }
        totp.deleteAll()
        totp.insert(totpEncrypted)
    }

    suspend fun decryptSecret(key: Crypto) = withContext(Dispatchers.IO) {
        val trashDecrypted = trash.getAll().map { it.copy(secret = key.decrypt(it.secret)) }
        trash.deleteAll()
        trash.insert(trashDecrypted)

        val hotpDecrypted = hotp.getAll().map { it.copy(secret = key.decrypt(it.secret)) }
        hotp.deleteAll()
        hotp.insert(hotpDecrypted)

        val totpDecrypted = totp.getAll().map { it.copy(secret = key.decrypt(it.secret)) }
        totp.deleteAll()
        totp.insert(totpDecrypted)
    }

    suspend fun encryptSecretByNewKey(current: Crypto, new: Crypto) = withContext(Dispatchers.IO) {
        val trashEncrypted = trash.getAll().map {
            val bytes = it.secret.decodeBase64()
            val decrypted = current.decrypt(bytes)
            it.copy(secret = new.encrypt(decrypted).toString(Charsets.UTF_8))
        }
        trash.deleteAll()
        trash.insert(trashEncrypted)

        val hotpEncrypted = hotp.getAll().map {
            val bytes = it.secret.decodeBase64()
            val decrypted = current.decrypt(bytes)
            it.copy(secret = new.encrypt(decrypted).toString(Charsets.UTF_8))
        }
        hotp.deleteAll()
        hotp.insert(hotpEncrypted)

        val totpEncrypted = totp.getAll().map {
            val bytes = it.secret.decodeBase64()
            val decrypted = current.decrypt(bytes)
            it.copy(secret = new.encrypt(decrypted).toString(Charsets.UTF_8))
        }
        totp.deleteAll()
        totp.insert(totpEncrypted)
    }
}