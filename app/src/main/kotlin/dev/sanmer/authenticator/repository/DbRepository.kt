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
    private val totp: TotpDao
) {
    fun getHotpAllAsFlow(enable: Boolean = true) = hotp.getMapToTrashAsFlow()
        .map { entries ->
            entries.mapNotNull { (hotp, trash) ->
                when {
                    enable -> if (trash == null) hotp.auth else null
                    else -> if (trash != null) hotp.auth else null
                }
            }
        }

    fun getHotpBySecretAsFlow(secret: String) = hotp.getBySecretAsFlow(secret)
        .filterNotNull()
        .map { it.auth }

    suspend fun existsHotp(secret: String) = withContext(Dispatchers.IO) {
        hotp.exists(secret)
    }

    fun getTotpAllAsFlow(enable: Boolean = true) = totp.getMapToTrashAsFlow()
        .map { entries ->
            entries.mapNotNull { (totp, trash) ->
                when {
                    enable -> if (trash == null) totp.auth else null
                    else -> if (trash != null) totp.auth else null
                }
            }
        }

    fun getTotpBySecretAsFlow(secret: String) = totp.getBySecretAsFlow(secret)
        .filterNotNull()
        .map { it.auth }

    suspend fun existsTotp(secret: String) = withContext(Dispatchers.IO) {
        totp.exists(secret)
    }

    suspend fun getTrashByTimestamp(before: Long) = withContext(Dispatchers.IO) {
        trash.getByTimestamp(before).map { it.secret }
    }

    suspend fun insertTrash(secret: String) = withContext(Dispatchers.IO) {
        trash.insert(TrashEntity(secret = secret))
    }

    suspend fun deleteTrash(secret: String) = withContext(Dispatchers.IO) {
        trash.delete(secret)
    }

    suspend fun deleteTrash(secrets: List<String>) = withContext(Dispatchers.IO) {
        trash.delete(secrets)
    }

    fun getAuthAllAsFlow(enable: Boolean = true) = combine(
        getHotpAllAsFlow(enable),
        getTotpAllAsFlow(enable)
    ) { hotp, totp ->
        hotp.toMutableList<Auth>().apply { addAll(totp) }.toList()
    }

    suspend fun getAuthBySecretAsFlow(secret: String): Flow<Auth> = when {
        existsHotp(secret) -> getHotpBySecretAsFlow(secret)
        existsTotp(secret) -> getTotpBySecretAsFlow(secret)
        else -> emptyFlow()
    }

    suspend fun insertAuth(values: List<Auth>) = withContext(Dispatchers.IO) {
        hotp.insert(values.filterIsInstance<HotpAuth>().map(::HotpEntity))
        totp.insert(values.filterIsInstance<TotpAuth>().map(::TotpEntity))
    }

    suspend fun updateAuth(value: Auth) = withContext(Dispatchers.IO) {
        when (value) {
            is HotpAuth -> hotp.update(HotpEntity(value))
            is TotpAuth -> totp.update(TotpEntity(value))
        }
    }

    suspend fun deleteAuth(value: Auth) = withContext(Dispatchers.IO) {
        when (value) {
            is HotpAuth -> hotp.delete(value.secret)
            is TotpAuth -> totp.delete(value.secret)
        }
    }

    suspend fun deleteAuth(secrets: List<String>) = withContext(Dispatchers.IO) {
        hotp.delete(secrets)
        totp.delete(secrets)
    }
}