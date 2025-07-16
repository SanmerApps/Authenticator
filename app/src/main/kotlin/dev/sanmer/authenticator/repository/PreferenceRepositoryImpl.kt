package dev.sanmer.authenticator.repository

import androidx.datastore.core.DataStore
import dev.sanmer.authenticator.datastore.model.Ntp
import dev.sanmer.authenticator.datastore.model.Preference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PreferenceRepositoryImpl(
    private val dataStore: DataStore<Preference>
) : PreferenceRepository {
    override val data = dataStore.data

    override suspend fun setKeyEncryptedByPassword(value: String) {
        withContext(Dispatchers.IO) {
            dataStore.updateData {
                it.copy(keyEncryptedByPassword = value)
            }
        }
    }

    override suspend fun setKeyEncryptedByBiometric(value: String) {
        withContext(Dispatchers.IO) {
            dataStore.updateData {
                it.copy(keyEncryptedByBiometric = value)
            }
        }
    }

    override suspend fun setNtpAddress(value: String) {
        withContext(Dispatchers.IO) {
            dataStore.updateData {
                it.copy(ntpAddress = value)
            }
        }
    }

    override suspend fun setNtp(value: Ntp) {
        withContext(Dispatchers.IO) {
            dataStore.updateData {
                it.copy(ntp = value)
            }
        }
    }
}
