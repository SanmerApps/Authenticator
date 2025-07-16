package dev.sanmer.authenticator.repository

import dev.sanmer.authenticator.datastore.model.Ntp
import dev.sanmer.authenticator.datastore.model.Preference
import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {
    val data: Flow<Preference>
    suspend fun setKeyEncryptedByPassword(value: String)
    suspend fun setKeyEncryptedByBiometric(value: String)
    suspend fun setNtpAddress(value: String)
    suspend fun setNtp(value: Ntp)
}