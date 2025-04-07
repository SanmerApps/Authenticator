package dev.sanmer.authenticator.repository

import androidx.datastore.core.DataStore
import dev.sanmer.authenticator.datastore.model.Ntp
import dev.sanmer.authenticator.datastore.model.Preference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceRepository @Inject constructor(
    private val dataStore: DataStore<Preference>
) {
    val data get() = dataStore.data

    suspend fun setKeyEncryptedByPassword(value: String) {
        withContext(Dispatchers.IO) {
            dataStore.updateData {
                it.copy(keyEncryptedByPassword = value)
            }
        }
    }

    suspend fun setKeyEncryptedByBiometric(value: String) {
        withContext(Dispatchers.IO) {
            dataStore.updateData {
                it.copy(keyEncryptedByBiometric = value)
            }
        }
    }

    suspend fun setNtpAddress(value: String) {
        withContext(Dispatchers.IO) {
            dataStore.updateData {
                it.copy(ntpAddress = value)
            }
        }
    }

    suspend fun setNtp(value: Ntp) {
        withContext(Dispatchers.IO) {
            dataStore.updateData {
                it.copy(ntp = value)
            }
        }
    }
}
