package dev.sanmer.authenticator

import dev.sanmer.ntp.NtpServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

interface Timer {
    val epochSeconds: StateFlow<Long>
    fun start()
    fun stop()

    companion object Default : Timer {
        private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        private var job: Job? = null

        override val epochSeconds = MutableStateFlow(System.currentTimeMillis())

        override fun start() {
            job = coroutineScope.launch {
                EpochSecond(
                    server = NtpServer.Apple,
                    coroutineScope = this
                ).collect(epochSeconds)
            }
        }

        override fun stop() {
            job?.cancel()
        }
    }

    private class EpochSecond(
        private val server: NtpServer,
        coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    ) : Flow<Long> {
        private var offset = 0L

        init {
            coroutineScope.launch {
                runCatching {
                    offset = server.sync()
                    Timber.d("Time offset(${server.address}): ${offset}ms")
                }.onFailure {
                    Timber.e(it)
                }
            }
        }

        override suspend fun collect(collector: FlowCollector<Long>) {
            while (currentCoroutineContext().isActive) {
                collector.emit((System.currentTimeMillis() + offset) / 1000)
                delay(1.seconds)
            }
        }
    }
}