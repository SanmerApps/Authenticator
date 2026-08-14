package dev.sanmer.authenticator.ui.screen.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sanmer.auth.ntp.NtpServer
import dev.sanmer.authenticator.Const.TIME_DISPLAY
import dev.sanmer.authenticator.Logger
import dev.sanmer.authenticator.database.model.Auth
import dev.sanmer.authenticator.ktx.stateIn
import dev.sanmer.authenticator.model.LoadData
import dev.sanmer.authenticator.repository.DbRepository
import dev.sanmer.authenticator.repository.OtpRepository
import dev.sanmer.authenticator.repository.TimeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime

class HomeViewModel(
    private val dbRepository: DbRepository,
    private val otpRepository: OtpRepository,
    private val timeRepository: TimeRepository
) : ViewModel() {
    val clock = timeRepository.clock

    val time = timeRepository.now
        .map {
            it.toLocalDateTime(TimeZone.currentSystemDefault()).time
                .format(TIME_DISPLAY)
        }

    var data by mutableStateOf<LoadData<List<Pair<Auth, StateFlow<String>>>>>(LoadData.Loading)
        private set

    private val logger = Logger.Android("HomeViewModel")

    init {
        logger.d("init")
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            dbRepository.getUntrashedAuthPropertiesAsFlow()
                .collectLatest { list ->
                    data = LoadData.Success(
                        list.sortedBy { it.auth.name.lowercase() }
                            .sortedBy { it.auth.issuer.lowercase() }
                            .map {
                                it.auth to otpRepository.otp(it)
                                    .stateIn(
                                        scope = viewModelScope,
                                        started = SharingStarted.Eagerly
                                    )
                            }
                    )
                }
        }
    }

    fun sync(server: NtpServer) {
        viewModelScope.launch {
            timeRepository.sync(server)
        }
    }
}