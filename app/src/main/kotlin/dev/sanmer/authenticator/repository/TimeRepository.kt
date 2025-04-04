package dev.sanmer.authenticator.repository

import dev.sanmer.authenticator.datastore.model.Ntp
import dev.sanmer.authenticator.datastore.model.Preference
import dev.sanmer.ntp.NtpServer
import dev.sanmer.otp.TOTP
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimeRepository @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    val ntpTime = MutableStateFlow(NtpServer.NtpTime())

    private var job: Job? = null
    private val _epochSeconds = MutableStateFlow(TOTP.epochSeconds)
    val epochSeconds get() = _epochSeconds.asStateFlow()

    init {
        preferenceObserver()
        ntpObserver()
    }

    private fun preferenceObserver() {
        coroutineScope.launch {
            preferenceRepository.data
                .distinctUntilChanged { old, new ->
                    old.ntp == new.ntp && old.ntpAddress == new.ntpAddress
                }
                .collectLatest {
                    sync(it)
                }
        }
    }

    private fun ntpObserver() {
        coroutineScope.launch {
            ntpTime.collectLatest {
                start(it)
            }
        }
    }

    private fun start(ntpTime: NtpServer.NtpTime) {
        job?.cancel()
        job = coroutineScope.launch {
            delay(1000 - (ntpTime.currentTimeMillis % 1000))
            while (currentCoroutineContext().isActive) {
                _epochSeconds.value = ntpTime.currentTimeMillis / 1000
                delay(1000)
            }
        }
    }

    fun sync(preference: Preference) {
        val server = when (preference.ntp) {
            Ntp.Custom -> NtpServer.Custom(preference.ntpAddress)
            Ntp.Apple -> NtpServer.Apple
            Ntp.Cloudflare -> NtpServer.Cloudflare
            Ntp.Google -> NtpServer.Google
            Ntp.Microsoft -> NtpServer.Microsoft
        }

        coroutineScope.launch {
            runCatching {
                ntpTime.updateAndGet { server.sync() }
            }.onSuccess {
                Timber.i("ntp(${server.address}): $it")
            }.onFailure {
                Timber.w(it)
            }
        }
    }
}