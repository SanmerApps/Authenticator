package dev.sanmer.authenticator.ui.screen.ntp

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sanmer.auth.ntp.NtpClock
import dev.sanmer.auth.ntp.NtpMessage
import dev.sanmer.auth.ntp.NtpServer
import dev.sanmer.authenticator.Logger
import dev.sanmer.authenticator.datastore.model.Ntp
import dev.sanmer.authenticator.model.LoadData
import dev.sanmer.authenticator.model.LoadData.Default.loadData
import dev.sanmer.authenticator.repository.PreferenceRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import kotlin.time.Duration

class NtpViewModel(
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {
    private val _list = mutableStateListOf(
        Ntp.Alibaba to NtpServer.Alibaba,
        Ntp.Apple to NtpServer.Apple,
        Ntp.Amazon to NtpServer.Amazon,
        Ntp.Cloudflare to NtpServer.Cloudflare,
        Ntp.Google to NtpServer.Google,
        Ntp.Meta to NtpServer.Meta,
        Ntp.Microsoft to NtpServer.Microsoft,
        Ntp.Tencent to NtpServer.Tencent
    )

    private val clocks = mutableStateMapOf<String, LoadData<NtpClock>>()

    val list by derivedStateOf {
        _list.sortedBy { (_, server) ->
            clock(server.address).getOrElse({ it.rtt }, Duration::INFINITE)
        }
    }
    val listState = LazyListState()

    val ntpAddress = TextFieldState()
    var bottomSheet by mutableStateOf<BottomSheet>(BottomSheet.None)

    private val logger = Logger.Android("NtpViewModel")

    init {
        logger.d("init")
        loadData()
        syncAll()
    }

    private fun loadData() {
        viewModelScope.launch {
            preferenceRepository.data
                .distinctUntilChangedBy { it.ntpAddress }
                .collect {
                    ntpAddress.setTextAndPlaceCursorAtEnd(it.ntpAddress)
                    val index = _list.indexOfFirst { (ntp, _) -> ntp == Ntp.Custom }
                    if (it.ntpAddress.isNotEmpty()) {
                        val server = NtpServer.Custom(it.ntpAddress).also(::sync)
                        if (index == -1) {
                            _list.add(Ntp.Custom to server)
                        } else {
                            _list[index] = Ntp.Custom to server
                        }
                    } else if (index != -1) {
                        _list.removeAt(index)
                    }
                }
        }
    }

    private fun syncAll() {
        viewModelScope.launch {
            _list.map { (_, server) ->
                async {
                    clocks[server.address] = LoadData.Loading
                    clocks[server.address] = loadData {
                        server.sync()
                    }.onFailure {
                        logger.w(it)
                    }
                }
            }.awaitAll()
            listState.requestScrollToItem(0)
        }
    }

    fun clock(address: String) = clocks.getOrDefault(address, LoadData.Pending)

    private fun sync(server: NtpServer) {
        viewModelScope.launch {
            when (clock(server.address)) {
                LoadData.Pending, is LoadData.Failure -> {
                    clocks[server.address] = LoadData.Loading
                    clocks[server.address] = loadData {
                        server.sync()
                    }.onSuccess {
                        listState.requestScrollToItem(0)
                    }.onFailure {
                        logger.w(it)
                    }
                }

                else -> {}
            }
        }
    }

    fun pick(ntp: Ntp, server: NtpServer) {
        if (clock(server.address).isSuccess) {
            viewModelScope.launch {
                preferenceRepository.setNtp(ntp)
            }
        } else {
            sync(server)
        }
    }

    fun setNtpAddress() {
        viewModelScope.launch {
            preferenceRepository.setNtpAddress(ntpAddress.text.trim().toString())
        }
    }

    sealed interface BottomSheet {
        data object None : BottomSheet
        data object Custom : BottomSheet
        data class NtpMsg(val ntp: Ntp, val msg: NtpMessage) : BottomSheet
    }
}