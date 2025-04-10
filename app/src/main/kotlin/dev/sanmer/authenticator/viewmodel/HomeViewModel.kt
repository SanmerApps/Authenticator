package dev.sanmer.authenticator.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.authenticator.model.auth.Auth
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
    val auths inline get() = loadState.auths

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
            dbRepository.getAuthAllAsFlow(enable = true)
                .collect { auths ->
                    loadState = LoadState.Ready(auths.sortedBy { it.issuer.lowercase() })
                }
        }
    }

    private fun clearTrash() {
        viewModelScope.launch {
            val secrets = dbRepository.getTrashAll(dead = true).map { it.secret }
            dbRepository.deleteAuth(secrets)
            dbRepository.deleteTrash(secrets)
        }
    }

    fun updateAuth(auth: Auth) {
        viewModelScope.launch {
            dbRepository.updateAuth(auth)
        }
    }

    fun recycleAuth(auth: Auth) {
        viewModelScope.launch {
            dbRepository.insertTrash(auth.secret)
        }
    }

    sealed class LoadState {
        abstract val auths: List<Auth>

        data object Pending : LoadState() {
            override val auths = emptyList<Auth>()
        }

        data class Ready(
            override val auths: List<Auth>
        ) : LoadState()

        val isPending inline get() = this is Pending
    }
}