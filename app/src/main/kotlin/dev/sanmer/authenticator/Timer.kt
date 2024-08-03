package dev.sanmer.authenticator

import dev.sanmer.ntp.NtpServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
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
        private var offset = 0L

        override val epochSeconds = MutableStateFlow(System.currentTimeMillis())

        override fun start() {
            sync()
            job = coroutineScope.launch {
                epochSeconds().collect(epochSeconds)
            }
        }

        override fun stop() {
            job?.cancel()
        }

        private fun sync() {
            coroutineScope.launch {
                runCatching {
                    offset = NtpServer.Apple.sync()
                    Timber.d("Time offset: $offset")
                }.onFailure {
                    Timber.e(it)
                }
            }
        }

        private fun epochSeconds() = flow {
            while (currentCoroutineContext().isActive) {
                emit((System.currentTimeMillis() + offset) / 1000)
                delay(1.seconds)
            }
        }
    }
}