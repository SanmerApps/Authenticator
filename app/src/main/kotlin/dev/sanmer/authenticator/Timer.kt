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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

object Timer {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val epochSecondsFlow = MutableStateFlow(System.currentTimeMillis())
    val epochSeconds get() = epochSecondsFlow.asStateFlow()

    private var offset = 0L

    init {
        sync()
    }

    fun start() {
        coroutineScope.launch {
            epochSeconds().collect(epochSecondsFlow)
        }
    }

    fun stop() {
        coroutineScope.cancel()
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