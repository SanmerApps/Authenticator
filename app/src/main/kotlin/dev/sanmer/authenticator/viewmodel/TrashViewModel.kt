package dev.sanmer.authenticator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.authenticator.database.entity.TrashEntity
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
import kotlin.time.Duration

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val dbRepository: DbRepository
) : ViewModel() {
    private val authsFlow = MutableStateFlow(emptyList<AuthWrapper>())
    val auths get() = authsFlow.asStateFlow()

    init {
        Timber.d("TrashViewModel init")
        dataObserver()
    }

    private fun dataObserver() {
        dbRepository.getAuthInTrashAllAsFlow()
            .onEach { source ->
                authsFlow.update {
                    source.map(::AuthWrapper).sortedBy {
                        it.auth.issuer.lowercase()
                    }
                }

            }.launchIn(viewModelScope)
    }

    fun restoreAuth(auth: Auth) {
        viewModelScope.launch {
            dbRepository.deleteTrash(auth.secret)
        }
    }

    fun restoreAuthAll() {
        viewModelScope.launch {
            dbRepository.deleteTrashAll()
        }
    }

    fun deleteAuth(auth: Auth) {
        viewModelScope.launch {
            dbRepository.deleteAuth(auth)
            dbRepository.deleteTrash(auth.secret)
        }
    }

    class AuthWrapper(
        val auth: Auth,
        val lifetime: Duration
    ) {
        constructor(value: Pair<Auth, TrashEntity>) : this(
            auth = value.first,
            lifetime = value.second.lifetime
        )
    }
}