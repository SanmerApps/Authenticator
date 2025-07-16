package dev.sanmer.authenticator.repository

import dev.sanmer.authenticator.database.dao.TotpDao
import dev.sanmer.authenticator.database.entity.TotpEntity
import dev.sanmer.authenticator.model.serializer.TotpAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DbRepositoryImpl(
    private val totp: TotpDao,
    private val secureRepository: SecureRepository
) : DbRepository {
    private suspend inline fun String.toEncrypted() = secureRepository.encrypt(this)

    private suspend inline fun String.toDecrypted() = secureRepository.decrypt(this)

    override suspend fun getTotpAllDecryptedAsFlow() = totp.getAllEnabledAsFlow()
        .map { entries -> entries.map { it.copy(secret = it.secret.toDecrypted()) } }

    override suspend fun getTotpDecryptedByIdAsFlow(id: Long) =
        totp.getByIdAsFlow(id).filterNotNull()
            .map { it.copy(secret = it.secret.toDecrypted()) }

    override suspend fun getTotpAllDecryptedTrashedAsFlow() = totp.getAllTrashedAsFlow()
        .map { entries -> entries.map { it.copy(secret = it.secret.toDecrypted()) } }

    override suspend fun getTotpAllTrashed(dead: Boolean) = withContext(Dispatchers.IO) {
        totp.getAllTrashed().filter { if (dead) it.lifetime > TotpEntity.LIFETIME_MAX else true }
    }

    override suspend fun insertTotp(auth: TotpAuth) = withContext(Dispatchers.IO) {
        totp.insert(
            TotpEntity(auth).copy(secret = auth.secret.toEncrypted())
        )
    }

    override suspend fun insertTotp(auths: List<TotpAuth>) = withContext(Dispatchers.IO) {
        totp.insert(
            auths.map { TotpEntity(it).copy(secret = it.secret.toEncrypted()) }
        )
    }

    override suspend fun updateTotp(id: Long, auth: TotpAuth) = withContext(Dispatchers.IO) {
        totp.update(
            TotpEntity(auth).copy(id = id, secret = auth.secret.toEncrypted())
        )
    }

    override suspend fun updateTotp(entity: TotpEntity) = withContext(Dispatchers.IO) {
        totp.update(
            entity.copy(secret = entity.secret.toEncrypted())
        )
    }

    override suspend fun updateTotp(entities: List<TotpEntity>) = withContext(Dispatchers.IO) {
        totp.update(
            entities.map { it.copy(secret = it.secret.toEncrypted()) }
        )
    }

    override suspend fun deleteTotp(entity: TotpEntity) = withContext(Dispatchers.IO) {
        totp.delete(entity)
    }

    override suspend fun deleteTotp(entities: List<TotpEntity>) = withContext(Dispatchers.IO) {
        totp.delete(entities)
    }
}