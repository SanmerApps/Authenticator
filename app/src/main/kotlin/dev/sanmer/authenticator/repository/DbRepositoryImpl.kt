package dev.sanmer.authenticator.repository

import dev.sanmer.auth.crypto.Crypto
import dev.sanmer.authenticator.database.dao.AuthDao
import dev.sanmer.authenticator.database.model.AuthProperties
import dev.sanmer.authenticator.database.model.AuthProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.time.Instant

class DbRepositoryImpl(
    private val authDao: AuthDao
) : DbRepository {
    private val mutex = Mutex()
    private var _key: Crypto = Crypto.Default

    private suspend inline fun <T> withKey(block: (Crypto) -> T) = mutex.withLock {
        block(_key)
    }

    override suspend fun setSessionKey(key: Crypto) = mutex.withLock {
        _key = key
    }

    override suspend fun getSessionKey() = withKey { it }

    override suspend fun encrypt(key: Crypto) = withContext(Dispatchers.IO) {
        val secrets = authDao.getProperties(AuthProperty.Key.Secret)
        val encrypted = secrets.map {
            it.copy(value = key.encrypt(it.value))
        }
        setSessionKey(key)
        authDao.upsertProperties(encrypted)
    }

    override suspend fun decrypt() = withContext(Dispatchers.IO) {
        val secrets = authDao.getProperties(AuthProperty.Key.Secret)
        val decrypted = withKey { current ->
            secrets.map {
                it.copy(value = current.decrypt(it.value))
            }
        }
        setSessionKey(Crypto.Default)
        authDao.upsertProperties(decrypted)
    }

    override suspend fun getAllAuthProperties() = withContext(Dispatchers.IO) {
        withKey { current ->
            authDao.getAllAuthProperties().map { auth ->
                auth.protectValue { current.decrypt(it) }
            }
        }
    }

    override suspend fun getUntrashedAuthProperties() = withContext(Dispatchers.IO) {
        withKey { current ->
            authDao.getUntrashedAuthProperties().map { auth ->
                auth.protectValue { current.decrypt(it) }
            }
        }
    }

    override suspend fun getUntrashedAuthPropertiesAsFlow() =
        authDao.getUntrashedAuthPropertiesAsFlow()
            .map { list ->
                withKey { current ->
                    list.map { auth ->
                        auth.protectValue { current.decrypt(it) }
                    }
                }
            }

    override fun getTrashedAuthAsFlow() = authDao.getTrashedAuthAsFlow()

    override fun getTrashedCountAsFlow() = authDao.getTrashedCountAsFlow()

    override suspend fun getAuthPropertiesAsFlow(id: Long) =
        authDao.getAuthPropertiesAsFlow(id)
            .filterNotNull()
            .map { auth ->
                withKey { current ->
                    auth.protectValue { current.decrypt(it) }
                }
            }

    override suspend fun upsert(auth: AuthProperties) = withContext(Dispatchers.IO) {
        val auth = withKey { current ->
            auth.protectValue { current.encrypt(it) }
        }
        authDao.upsert(
            auth = auth.auth,
            properties = auth.properties
        )
    }

    override suspend fun trash(authId: Long, trashedAt: Instant) = withContext(Dispatchers.IO) {
        authDao.trashAuth(authId, trashedAt)
    }

    override suspend fun delete(authId: Long) = withContext(Dispatchers.IO) {
        authDao.delete(authId)
    }
}