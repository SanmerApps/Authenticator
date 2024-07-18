package dev.sanmer.authenticator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.authenticator.model.auth.Auth
import dev.sanmer.authenticator.repository.DbRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val dbRepository: DbRepository
) : ViewModel() {
    private val authsFlow = MutableStateFlow(emptyList<Auth>())
    val auths get() = authsFlow.asStateFlow()

    init {
        Timber.d("TrashViewModel init")
        dataObserver()
    }

    private fun dataObserver() {
        dbRepository.getAuthAllAsFlow(enable = false)
            .onEach { source ->
                authsFlow.update {
                    source.sortedBy {
                        it.issuer.lowercase()
                    }
                }

            }.launchIn(viewModelScope)
    }

    fun restoreAuth(auth: Auth) {
        viewModelScope.launch {
            dbRepository.deleteTrash(auth.secret)
        }
    }

    fun deleteAuth(auth: Auth) {
        viewModelScope.launch {
            dbRepository.deleteAuth(auth)
            dbRepository.deleteTrash(auth.secret)
        }
    }
}