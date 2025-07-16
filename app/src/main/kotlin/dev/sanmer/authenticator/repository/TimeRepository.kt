package dev.sanmer.authenticator.repository

import dev.sanmer.authenticator.datastore.model.Preference
import dev.sanmer.ntp.NtpServer
import kotlinx.coroutines.flow.StateFlow

interface TimeRepository {
    val ntpTime: StateFlow<NtpServer.NtpTime>
    val epochSeconds: StateFlow<Long>
    suspend fun sync(preference: Preference): Result<NtpServer.NtpTime>
}