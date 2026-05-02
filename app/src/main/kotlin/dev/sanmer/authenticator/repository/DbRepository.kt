package dev.sanmer.authenticator.repository

import dev.sanmer.authenticator.database.entity.TotpEntity
import dev.sanmer.crypto.Crypto
import kotlinx.coroutines.flow.Flow

interface DbRepository {
    fun setSessionKey(key: Crypto)
    suspend fun encrypt(key: Crypto)
    suspend fun decrypt(key: Crypto)
    suspend fun reEncrypt(old: Crypto, new: Crypto)

    suspend fun getTotpAllDecryptedAsFlow(): Flow<List<TotpEntity>>
    suspend fun getTotpDecryptedByIdAsFlow(id: Long): Flow<TotpEntity>
    suspend fun getTotpAllDecryptedTrashedAsFlow(): Flow<List<TotpEntity>>
    suspend fun getTotpAllTrashed(dead: Boolean): List<TotpEntity>
    suspend fun insertTotp(entity: TotpEntity)
    suspend fun insertTotp(entities: List<TotpEntity>)
    suspend fun updateTotp(entity: TotpEntity)
    suspend fun updateTotp(entities: List<TotpEntity>)
    suspend fun deleteTotp(entity: TotpEntity)
    suspend fun deleteTotp(entities: List<TotpEntity>)
}