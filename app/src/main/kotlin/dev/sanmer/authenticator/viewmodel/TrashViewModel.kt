package dev.sanmer.authenticator.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.authenticator.model.auth.Auth
import dev.sanmer.authenticator.repository.DbRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val dbRepository: DbRepository
) : ViewModel() {
    var loadState by mutableStateOf<LoadState>(LoadState.Pending)
        private set
    val auths inline get() = loadState.auths

    init {
        Timber.d("TrashViewModel init")
        dataObserver()
    }

    private fun dataObserver() {
        viewModelScope.launch {
            dbRepository.getAuthInTrashAllAsFlow()
                .collect { source ->
                    loadState = LoadState.Ready(
                        source.map(::AuthCompat).sortedBy {
                            it.auth.issuer.lowercase()
                        }
                    )
                }
        }
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

    data class AuthCompat(
        val auth: Auth,
        val lifetime: Duration
    ) {
        constructor(value: Pair<Auth, Duration>) : this(
            auth = value.first,
            lifetime = value.second
        )
    }

    sealed class LoadState {
        abstract val auths: List<AuthCompat>

        data object Pending : LoadState() {
            override val auths = emptyList<AuthCompat>()
        }

        data class Ready(
            override val auths: List<AuthCompat>
        ) : LoadState()

        val isPending inline get() = this is Pending
    }
}