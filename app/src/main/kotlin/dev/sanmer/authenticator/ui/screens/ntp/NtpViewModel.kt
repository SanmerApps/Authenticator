package dev.sanmer.authenticator.ui.screens.ntp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sanmer.authenticator.Logger
import dev.sanmer.authenticator.datastore.model.Ntp
import dev.sanmer.authenticator.repository.PreferenceRepository
import dev.sanmer.authenticator.repository.TimeRepository
import dev.sanmer.logo.Brand
import dev.sanmer.ntp.NtpServer
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration

class NtpViewModel(
    private val preferenceRepository: PreferenceRepository,
    private val timeRepository: TimeRepository
) : ViewModel() {
    private val _ntps = MutableStateFlow(defaultNtps)
    val ntps = _ntps.map { list ->
        list.sortedBy { it.ntpTime.rtt }
    }

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Pending)
    val syncState = _syncState.asStateFlow()

    private val logger = Logger.Android("NtpViewModel")

    init {
        logger.d("init")
        timeObserver()
        syncAll()
    }

    private fun timeObserver() {
        viewModelScope.launch {
            timeRepository.ntpTime
                .collect { ntpTime ->
                    val preference = preferenceRepository.data.first()
                    _ntps.update { ntps ->
                        val index = ntps.indexOfFirst { it.ntp == preference.ntp }
                        ntps.toMutableList().apply {
                            set(index, ntps[index].copy(ntpTime = ntpTime))
                        }
                    }
                }
        }
    }

    fun syncAll() {
        viewModelScope.launch {
            val preference = preferenceRepository.data.first()
            _syncState.update { SyncState.Ready(defaultNtps.size) }
            defaultNtps.mapIndexed { index, ntp ->
                async {
                    val ntpTime = if (ntp.ntp == preference.ntp) {
                        timeRepository.sync(preference)
                    } else {
                        sync(ntp.server)
                    }.getOrDefault(
                        NtpServer.NtpTime(
                            Duration.Companion.INFINITE,
                            Duration.Companion.ZERO
                        )
                    )

                    _syncState.update { SyncState.Updating(it.size, it.finished + 1) }
                    _ntps.update {
                        it.toMutableList().apply { set(index, ntp.copy(ntpTime = ntpTime)) }
                    }
                }
            }.awaitAll()

            _syncState.update { SyncState.Finished(it.size, it.finished) }
        }
    }

    fun setNtp(ntp: Ntp) {
        viewModelScope.launch {
            preferenceRepository.setNtp(ntp)
        }
    }

    private suspend fun sync(server: NtpServer) =
        runCatching {
            server.sync()
        }.onSuccess {
            logger.d("ntp(${server.address}): $it")
        }.onFailure {
            logger.e(it)
        }

    data class NtpCompat(
        val brand: Brand,
        val ntp: Ntp,
        val server: NtpServer,
        val ntpTime: NtpServer.NtpTime
    )

    sealed class SyncState {
        abstract val size: Int
        abstract val finished: Int

        data object Pending : SyncState() {
            override val size = 1
            override val finished = -1
        }

        data class Ready(
            override val size: Int
        ) : SyncState() {
            override val finished = 0
        }

        data class Updating(
            override val size: Int,
            override val finished: Int
        ) : SyncState()

        data class Finished(
            override val size: Int,
            override val finished: Int
        ) : SyncState()

        val progress inline get() = finished / size.toFloat()

        val isRunning inline get() = this is Ready || this is Updating

    }

    companion object Default {
        private val defaultNtps = listOf(
            NtpCompat(
                brand = Brand.Aliyun,
                ntp = Ntp.Alibaba,
                server = NtpServer.Alibaba,
                ntpTime = NtpServer.NtpTime()
            ),
            NtpCompat(
                brand = Brand.Apple,
                ntp = Ntp.Apple,
                server = NtpServer.Apple,
                ntpTime = NtpServer.NtpTime()
            ),
            NtpCompat(
                brand = Brand.AWS,
                ntp = Ntp.Amazon,
                server = NtpServer.Amazon,
                ntpTime = NtpServer.NtpTime()
            ),
            NtpCompat(
                brand = Brand.Cloudflare,
                ntp = Ntp.Cloudflare,
                server = NtpServer.Cloudflare,
                ntpTime = NtpServer.NtpTime()
            ),
            NtpCompat(
                brand = Brand.Google,
                ntp = Ntp.Google,
                server = NtpServer.Google,
                ntpTime = NtpServer.NtpTime()
            ),
            NtpCompat(
                brand = Brand.Meta,
                ntp = Ntp.Meta,
                server = NtpServer.Meta,
                ntpTime = NtpServer.NtpTime()
            ),
            NtpCompat(
                brand = Brand.Microsoft,
                ntp = Ntp.Microsoft,
                server = NtpServer.Microsoft,
                ntpTime = NtpServer.NtpTime()
            ),
            NtpCompat(
                brand = Brand.TencentCloud,
                ntp = Ntp.Tencent,
                server = NtpServer.Tencent,
                ntpTime = NtpServer.NtpTime()
            )
        )
    }
}