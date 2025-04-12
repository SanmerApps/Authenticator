package dev.sanmer.authenticator.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.authenticator.database.entity.TotpEntity
import dev.sanmer.authenticator.model.impl.TotpImpl
import dev.sanmer.authenticator.repository.DbRepository
import dev.sanmer.authenticator.repository.TimeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dbRepository: DbRepository,
    private val timeRepository: TimeRepository
) : ViewModel() {
    var loadState by mutableStateOf<LoadState>(LoadState.Pending)
        private set
    val totp inline get() = loadState.totp
    val isPending inline get() = loadState.isPending

    val time get() = timeRepository.epochSeconds.map { epochSecond ->
        Instant.ofEpochSecond(epochSecond)
            .atZone(ZoneId.systemDefault())
            .toLocalTime()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = LocalTime.now()
    )

    init {
        Timber.d("HomeViewModel init")
        dataObserver()
        clearTrash()
    }

    private fun dataObserver() {
        viewModelScope.launch {
            dbRepository.getTotpAllDecryptedAsFlow()
                .collect { totp ->
                    loadState = LoadState.Ready(
                        totp = totp.map {
                            TotpImpl(
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

    fun recycle(entity: TotpEntity) {
        viewModelScope.launch {
            dbRepository.updateTotp(entity.copy(deletedAt = System.currentTimeMillis()))
        }
    }

    sealed class LoadState {
        abstract val totp: List<TotpImpl>

        data object Pending : LoadState() {
            override val totp = emptyList<TotpImpl>()
        }

        data class Ready(
            override val totp: List<TotpImpl>
        ) : LoadState()

        val isPending inline get() = this is Pending
    }
}