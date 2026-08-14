package dev.sanmer.authenticator.repository

import dev.sanmer.auth.crypto.Crypto
import dev.sanmer.authenticator.database.model.Auth
import dev.sanmer.authenticator.database.model.AuthProperties
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

interface DbRepository {
    suspend fun setSessionKey(key: Crypto)
    suspend fun getSessionKey(): Crypto
    suspend fun encrypt(key: Crypto)
    suspend fun decrypt()

    suspend fun getAllAuthProperties(): List<AuthProperties>
    suspend fun getUntrashedAuthProperties(): List<AuthProperties>
    suspend fun getUntrashedAuthPropertiesAsFlow(): Flow<List<AuthProperties>>
    fun getTrashedAuthAsFlow(): Flow<List<Auth>>
    fun getTrashedCountAsFlow(): Flow<Int>
    suspend fun getAuthPropertiesAsFlow(id: Long): Flow<AuthProperties>
    suspend fun upsert(auth: AuthProperties)
    suspend fun trash(authId: Long, trashedAt: Instant)
    suspend fun delete(authId: Long)
}