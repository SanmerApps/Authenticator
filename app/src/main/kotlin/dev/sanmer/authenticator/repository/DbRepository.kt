package dev.sanmer.authenticator.repository

import dev.sanmer.authenticator.database.dao.HotpDao
import dev.sanmer.authenticator.database.dao.TotpDao
import dev.sanmer.authenticator.database.dao.TrashDao
import dev.sanmer.authenticator.database.entity.HotpEntity
import dev.sanmer.authenticator.database.entity.TotpEntity
import dev.sanmer.authenticator.database.entity.TrashEntity
import dev.sanmer.authenticator.model.auth.Auth
import dev.sanmer.authenticator.model.auth.HotpAuth
import dev.sanmer.authenticator.model.auth.TotpAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DbRepository @Inject constructor(
    private val trash: TrashDao,
    private val hotp: HotpDao,
    private val totp: TotpDao,
    private val timeRepository: TimeRepository,
    private val secureRepository: SecureRepository
) {
    private suspend inline fun String.toDecrypted() = secureRepository.decrypt(this)

    private suspend inline fun String.toEncrypted() = secureRepository.encrypt(this)

    private suspend inline fun HotpEntity.toDecrypted() = auth()
        .let { it.copy(secret = it.secret.toDecrypted()) }

    private suspend inline fun HotpAuth.toEncrypted() = copy(secret = secret.toEncrypted())
        .let(::HotpEntity)

    private suspend inline fun TotpEntity.toDecrypted() = auth(timeRepository.epochSeconds)
        .let { it.copy(secret = it.secret.toDecrypted()) }

    private suspend inline fun TotpAuth.toEncrypted() = copy(secret = secret.toEncrypted())
        .let(::TotpEntity)

    private suspend inline fun TrashEntity.toDecrypted() = copy(secret = secret.toDecrypted())

    private fun getHotpAllAsFlow(enable: Boolean = true) = hotp.getMapToTrashAsFlow()
        .map { entries ->
            entries.mapNotNull { (hotp, trash) ->
                when {
                    enable -> if (trash == null) hotp.toDecrypted() else null
                    else -> if (trash != null) hotp.toDecrypted() else null
                }
            }
        }

    private suspend fun getHotpBySecretAsFlow(secret: String) = hotp.getBySecretAsFlow(
        secret = secret.toEncrypted()
    ).filterNotNull()
        .map { it.toDecrypted() }

    private suspend fun existsHotp(secret: String) = withContext(Dispatchers.IO) {
        hotp.exists(secret.toEncrypted())
    }

    private fun getTotpAllAsFlow(enable: Boolean = true) = totp.getMapToTrashAsFlow()
        .map { entries ->
            entries.mapNotNull { (totp, trash) ->
                when {
                    enable -> if (trash == null) totp.toDecrypted() else null
                    else -> if (trash != null) totp.toDecrypted() else null
                }
            }
        }

    private suspend fun getTotpBySecretAsFlow(secret: String) = totp.getBySecretAsFlow(
        secret = secret.toEncrypted()
    ).filterNotNull()
        .map { it.toDecrypted() }

   private suspend fun existsTotp(secret: String) = withContext(Dispatchers.IO) {
        totp.exists(secret.toEncrypted())
    }

    suspend fun getTrashAll(dead: Boolean = false) = withContext(Dispatchers.IO) {
        when {
            dead -> trash.getAll().filter { it.lifetime >= TrashEntity.LIFETIME_MAX }
            else -> trash.getAll()
        }.map { it.toDecrypted() }
    }

    suspend fun insertTrash(secret: String) = withContext(Dispatchers.IO) {
        trash.insert(TrashEntity(secret = secret.toEncrypted()))
    }

    suspend fun insertTrash(secrets: List<String>) = withContext(Dispatchers.IO) {
        val timestamp: Long = System.currentTimeMillis()
        trash.insert(
            secrets.map { TrashEntity(secret = it.toEncrypted(), timestamp = timestamp) }
        )
    }

    suspend fun deleteTrash(secret: String) = withContext(Dispatchers.IO) {
        trash.delete(secret.toEncrypted())
    }

    suspend fun deleteTrash(secrets: List<String>) = withContext(Dispatchers.IO) {
        trash.delete(secrets.map { it.toEncrypted() })
    }

    suspend fun deleteTrashAll() = withContext(Dispatchers.IO) {
        trash.deleteAll()
    }

    fun getAuthAllAsFlow(enable: Boolean = true) = combine(
        getHotpAllAsFlow(enable),
        getTotpAllAsFlow(enable)
    ) { hotp, totp ->
        hotp.toMutableList<Auth>().apply { addAll(totp) }.toList()
    }

    fun getAuthInTrashAllAsFlow() = combine(
        hotp.getAllWithTrashAsFlow().map { entries -> entries.mapKeys { it.key.toDecrypted() }.toList() },
        totp.getAllWithTrashAsFlow().map { entries -> entries.mapKeys { it.key.toDecrypted() }.toList() }
    ) { hotp, totp ->
        hotp.toMutableList<Pair<Auth, TrashEntity>>().apply { addAll(totp) }
    }

    suspend fun getAuthBySecretAsFlow(secret: String): Flow<Auth> = when {
        existsHotp(secret) -> getHotpBySecretAsFlow(secret)
        existsTotp(secret) -> getTotpBySecretAsFlow(secret)
        else -> emptyFlow()
    }

    suspend fun insertAuth(values: List<Auth>) = withContext(Dispatchers.IO) {
        hotp.insert(values.filterIsInstance<HotpAuth>().map { it.toEncrypted() })
        totp.insert(values.filterIsInstance<TotpAuth>().map { it.toEncrypted() })
    }

    suspend fun updateAuth(value: Auth) = withContext(Dispatchers.IO) {
        when (value) {
            is HotpAuth -> hotp.update(value.toEncrypted())
            is TotpAuth -> totp.update(value.toEncrypted())
        }
    }

    suspend fun deleteAuth(value: Auth) = withContext(Dispatchers.IO) {
        when (value) {
            is HotpAuth -> hotp.delete(value.secret.toEncrypted())
            is TotpAuth -> totp.delete(value.secret.toEncrypted())
        }
    }

    suspend fun deleteAuth(secrets: List<String>) = withContext(Dispatchers.IO) {
        hotp.delete(secrets.map { it.toEncrypted() })
        totp.delete(secrets.map { it.toEncrypted() })
    }
}