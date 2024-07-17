package dev.sanmer.authenticator.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.authenticator.model.auth.Auth
import dev.sanmer.authenticator.model.auth.HotpAuth
import dev.sanmer.authenticator.repository.DbRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dbRepository: DbRepository
) : ViewModel() {
    private val authsFlow = MutableStateFlow(emptyList<Auth>())
    val auths get() = authsFlow.asStateFlow()

    var isSearch by mutableStateOf(false)
        private set
    private val keyFlow = MutableStateFlow("")

    init {
        Timber.d("HomeViewModel init")
        dataObserver()
    }

    private fun dataObserver() {
        combine(
            dbRepository.authsFlow,
            keyFlow
        ) { source, key ->
            authsFlow.update {
                source.filter {
                    if (key.isNotBlank()) {
                        it.name.contains(key, ignoreCase = true)
                                || it.issuer.contains(key, ignoreCase = true)
                    } else {
                        true
                    }
                }
            }

        }.launchIn(viewModelScope)
    }

    fun search(key: String) {
        keyFlow.update { key }
    }

    fun openSearch() {
        isSearch = true
    }

    fun closeSearch() {
        isSearch = false
        keyFlow.update { "" }
    }

    fun updateHotp(auth: HotpAuth) {
        viewModelScope.launch {
            dbRepository.updateHotp(auth)
        }
    }
}