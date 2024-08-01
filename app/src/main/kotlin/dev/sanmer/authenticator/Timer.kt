package dev.sanmer.authenticator

import dev.sanmer.ntp.NtpServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

object Timer {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val epochSecondsFlow = MutableStateFlow(Clock.System.now().epochSeconds)
    val epochSeconds get() = epochSecondsFlow.asStateFlow()

    fun start() {
        coroutineScope.launch {
            val offset = offset()
            Timber.d("Time offset: $offset")
            while (currentCoroutineContext().isActive) {
                epochSecondsFlow.value = (System.currentTimeMillis() + offset) / 1000
                delay(1.seconds)
            }
        }
    }

    fun stop() {
        coroutineScope.cancel()
    }

    private suspend fun offset() =
        runCatching { NtpServer.Apple.sync() }.getOrElse { 0L }
}