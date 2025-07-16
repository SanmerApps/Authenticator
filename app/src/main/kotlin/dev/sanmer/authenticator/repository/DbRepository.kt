package dev.sanmer.authenticator.repository

import dev.sanmer.authenticator.database.entity.TotpEntity
import dev.sanmer.authenticator.model.serializer.TotpAuth
import kotlinx.coroutines.flow.Flow

interface DbRepository {
    suspend fun getTotpAllDecryptedAsFlow(): Flow<List<TotpEntity>>
    suspend fun getTotpDecryptedByIdAsFlow(id: Long): Flow<TotpEntity>
    suspend fun getTotpAllDecryptedTrashedAsFlow(): Flow<List<TotpEntity>>
    suspend fun getTotpAllTrashed(dead: Boolean): List<TotpEntity>
    suspend fun insertTotp(auth: TotpAuth)
    suspend fun insertTotp(auths: List<TotpAuth>)
    suspend fun updateTotp(id: Long, auth: TotpAuth)
    suspend fun updateTotp(entity: TotpEntity)
    suspend fun updateTotp(entities: List<TotpEntity>)
    suspend fun deleteTotp(entity: TotpEntity)
    suspend fun deleteTotp(entities: List<TotpEntity>)
}