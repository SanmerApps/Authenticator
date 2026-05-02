package dev.sanmer.authenticator.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sanmer.authenticator.Logger
import dev.sanmer.authenticator.database.entity.TotpEntity
import dev.sanmer.authenticator.model.auth.TotpAuth
import dev.sanmer.authenticator.repository.DbRepository
import dev.sanmer.authenticator.repository.TimeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId

class HomeViewModel(
    private val dbRepository: DbRepository,
    private val timeRepository: TimeRepository
) : ViewModel() {
    var loadState by mutableStateOf<LoadState>(LoadState.Pending)
        private set
    val totp inline get() = loadState.totp
    val isPending inline get() = loadState.isPending

    val time = timeRepository.epochSeconds.map { epochSecond ->
        Instant.ofEpochSecond(epochSecond)
            .atZone(ZoneId.systemDefault())
            .toLocalTime()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = LocalTime.now()
    )

    private val logger = Logger.Android("HomeViewModel")

    init {
        logger.d("init")
        dataObserver()
        clearTrash()
    }

    private fun dataObserver() {
        viewModelScope.launch {
            dbRepository.getTotpAllDecryptedAsFlow()
                .collect { totp ->
                    loadState = LoadState.Ready(
                        totp = totp.map {
                            TotpAuth(
                                entity = it,
                                epochSeconds = timeRepository.epochSeconds
                            )
                        }.sortedBy { it.entity.issuer.lowercase() },
                    )
                }
        }
    }

    private fun clearTrash() {
        viewModelScope.launch {
            dbRepository.deleteTotp(
                dbRepository.getTotpAllTrashed(dead = true)
            )
        }
    }

    fun moveToTrash(entity: TotpEntity) {
        viewModelScope.launch {
            dbRepository.updateTotp(entity.toTrash())
        }
    }

    sealed class LoadState {
        abstract val totp: List<TotpAuth>

        data object Pending : LoadState() {
            override val totp = emptyList<TotpAuth>()
        }

        data class Ready(
            override val totp: List<TotpAuth>
        ) : LoadState()

        val isPending inline get() = this is Pending
    }
}