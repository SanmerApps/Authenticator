package dev.sanmer.authenticator.repository

import dev.sanmer.auth.ntp.NtpClock
import dev.sanmer.auth.ntp.NtpServer
import dev.sanmer.authenticator.model.LoadData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Clock
import kotlin.time.Instant

interface TimeRepository : Clock {
    val clock: StateFlow<LoadData<NtpClock>>
    val now: Flow<Instant>
    suspend fun sync(server: NtpServer)
}