package dev.sanmer.authenticator.repository

import dev.sanmer.auth.ntp.NtpClock
import dev.sanmer.auth.ntp.NtpServer
import dev.sanmer.authenticator.Logger
import dev.sanmer.authenticator.model.LoadData
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import java.net.SocketTimeoutException
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

class TimeRepositoryImpl : TimeRepository {
    private val _clock = MutableStateFlow<LoadData<NtpClock>>(LoadData.Pending)

    override val clock = _clock.asStateFlow()

    override val now = flow {
        while (currentCoroutineContext().isActive) {
            val now = now()
            emit(now)
            delay(1.seconds - now.nanosecondsOfSecond.nanoseconds)
        }
    }

    private val logger = Logger.Android("NTP")

    override fun now() = clock.value.getOrElse({ it }) { Clock.System }.now()

    override suspend fun sync(server: NtpServer) {
        _clock.update { LoadData.Loading }
        var data: LoadData<NtpClock> = LoadData.Pending
        for (times in 1..3) {
            try {
                data = LoadData.Success(server.sync())
            } catch (e: SocketTimeoutException) {
                data = LoadData.Failure(e)
                delay(200.milliseconds * times)
            } catch (e: Throwable) {
                data = LoadData.Failure(e)
                logger.e(e)
            }
        }
        _clock.update { data }
    }
}