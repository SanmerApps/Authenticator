package dev.sanmer.authenticator.repository

import dev.sanmer.authenticator.database.dao.HotpDao
import dev.sanmer.authenticator.database.dao.TotpDao
import dev.sanmer.authenticator.database.entity.HotpEntity
import dev.sanmer.authenticator.database.entity.TotpEntity
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
    private val hotp: HotpDao,
    private val totp: TotpDao
) {
    val hotpFlow = hotp.getAllAsFlow().map { values -> values.map { it.auth } }

    fun getHotpBySecretAsFlow(secret: String) = hotp.getBySecretAsFlow(secret)
        .filterNotNull()
        .map { it.auth }

    suspend fun insertHotp(value: HotpAuth) = withContext(Dispatchers.IO) {
        hotp.insert(HotpEntity(value))
    }

    suspend fun insertHotp(values: List<HotpAuth>) = withContext(Dispatchers.IO) {
        hotp.insert(values.map(::HotpEntity))
    }

    suspend fun updateHotp(value: HotpAuth) = withContext(Dispatchers.IO) {
        hotp.update(HotpEntity(value))
    }

    suspend fun deleteHotp(value: HotpAuth) = withContext(Dispatchers.IO) {
        hotp.delete(HotpEntity(value))
    }

    suspend fun deleteHotp(values: List<HotpAuth>) = withContext(Dispatchers.IO) {
        hotp.delete(values.map(::HotpEntity))
    }

    suspend fun existsHotp(secret: String) = withContext(Dispatchers.IO) {
        hotp.exists(secret)
    }

    val totpFlow = totp.getAllAsFlow().map { values -> values.map { it.auth } }

    fun getTotpBySecretAsFlow(secret: String) = totp.getBySecretAsFlow(secret)
        .filterNotNull()
        .map { it.auth }

    suspend fun insertTotp(value: TotpAuth) = withContext(Dispatchers.IO) {
        totp.insert(TotpEntity(value))
    }

    suspend fun insertTotp(values: List<TotpAuth>) = withContext(Dispatchers.IO) {
        totp.insert(values.map(::TotpEntity))
    }

    suspend fun updateTotp(value: TotpAuth) = withContext(Dispatchers.IO) {
        totp.update(TotpEntity(value))
    }

    suspend fun deleteTotp(value: TotpAuth) = withContext(Dispatchers.IO) {
        totp.delete(TotpEntity(value))
    }

    suspend fun deleteTotp(values: List<TotpAuth>) = withContext(Dispatchers.IO) {
        totp.delete(values.map(::TotpEntity))
    }

    suspend fun existsTotp(secret: String) = withContext(Dispatchers.IO) {
        totp.exists(secret)
    }

    val authsFlow = combine(
        hotpFlow,
        totpFlow
    ) { hotp, totp ->
        hotp.toMutableList<Auth>().apply { addAll(totp) }.toList()
    }

    suspend fun getBySecretAsFlow(secret: String): Flow<Auth> {
        return when {
            existsHotp(secret) -> getHotpBySecretAsFlow(secret)
            existsTotp(secret) -> getTotpBySecretAsFlow(secret)
            else -> emptyFlow()
        }
    }

    suspend fun insert(values: List<Auth>) {
        insertHotp(values.filterIsInstance<HotpAuth>())
        insertTotp(values.filterIsInstance<TotpAuth>())
    }

    suspend fun update(value: Auth) = when (value) {
        is HotpAuth -> {
            if (existsHotp(value.secret)) {
                updateHotp(value)
            } else {
                insertHotp(value)
            }
        }

        is TotpAuth -> {
            if (existsTotp(value.secret)) {
                updateTotp(value)
            } else {
                insertTotp(value)
            }
        }

        else -> {}
    }

    suspend fun delete(value: Auth) = when (value) {
        is HotpAuth -> deleteHotp(value)
        is TotpAuth -> deleteTotp(value)
        else -> {}
    }
}