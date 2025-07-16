package dev.sanmer.authenticator.repository

import dev.sanmer.authenticator.Logger
import dev.sanmer.authenticator.datastore.model.Ntp
import dev.sanmer.authenticator.datastore.model.Preference
import dev.sanmer.ntp.NtpServer
import dev.sanmer.otp.TOTP
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TimeRepositoryImpl(
    private val preferenceRepository: PreferenceRepository
) : TimeRepository {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var job = SupervisorJob()

    private val _ntpTime = MutableStateFlow(NtpServer.NtpTime())
    override val ntpTime get() = _ntpTime.asStateFlow()

    private val _epochSeconds = MutableStateFlow(TOTP.epochSeconds)
    override val epochSeconds get() = _epochSeconds.asStateFlow()

    private val logger = Logger.Android("TimeRepositoryImpl")

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
            _ntpTime.collect {
                start(it)
            }
        }
    }

    private fun start(ntpTime: NtpServer.NtpTime) {
        if (job.isActive) {
            job.cancel()
            job = SupervisorJob()
        }
        coroutineScope.launch(job) {
            delay(1000 - (ntpTime.currentTimeMillis % 1000))
            while (isActive) {
                _epochSeconds.value = ntpTime.currentTimeMillis / 1000
                delay(1000)
            }
        }
    }

    override suspend fun sync(preference: Preference): Result<NtpServer.NtpTime> {
        val server = when (preference.ntp) {
            Ntp.Custom -> NtpServer.Custom(preference.ntpAddress)
            Ntp.Alibaba -> NtpServer.Alibaba
            Ntp.Apple -> NtpServer.Apple
            Ntp.Amazon -> NtpServer.Amazon
            Ntp.Cloudflare -> NtpServer.Cloudflare
            Ntp.Google -> NtpServer.Google
            Ntp.Meta -> NtpServer.Meta
            Ntp.Microsoft -> NtpServer.Microsoft
            Ntp.Tencent -> NtpServer.Tencent
        }

        return runCatching {
            _ntpTime.updateAndGet { server.sync() }
        }.onSuccess {
            logger.i("ntp(${server.address}): $it")
        }.onFailure {
            logger.e(it)
        }
    }
}