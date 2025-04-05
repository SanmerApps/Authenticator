package dev.sanmer.authenticator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.authenticator.model.auth.Auth
import dev.sanmer.authenticator.repository.DbRepository
import dev.sanmer.authenticator.repository.TimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
    private val _auths = MutableStateFlow(emptyList<Auth>())
    val auths get() = _auths.asStateFlow()

    val time = timeRepository.epochSeconds.map { epochSecond ->
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
                    _auths.update {
                        auths.sortedBy { it.issuer.lowercase() }
                    }
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
}