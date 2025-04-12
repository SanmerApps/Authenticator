package dev.sanmer.authenticator.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.authenticator.database.entity.TotpEntity
import dev.sanmer.authenticator.repository.DbRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val dbRepository: DbRepository
) : ViewModel() {
    var loadState by mutableStateOf<LoadState>(LoadState.Pending)
        private set
    val totp inline get() = loadState.totp
    val isPending inline get() = loadState.isPending

    init {
        Timber.d("TrashViewModel init")
        dataObserver()
    }

    private fun dataObserver() {
        viewModelScope.launch {
            dbRepository.getTotpAllTrashedAsFlow()
                .collect { totp ->
                    loadState = LoadState.Ready(totp)
                }
        }
    }

    fun restore(entity: TotpEntity) {
        viewModelScope.launch {
            dbRepository.updateTotp(
                entity = entity.copy(deletedAt = 0),
                encrypt = false
            )
        }
    }

    fun delete(entity: TotpEntity) {
        viewModelScope.launch {
            dbRepository.deleteTotp(entity)
        }
    }

    sealed class LoadState {
        abstract val totp: List<TotpEntity>

        data object Pending : LoadState() {
            override val totp = emptyList<TotpEntity>()
        }

        data class Ready(
            override val totp: List<TotpEntity>
        ) : LoadState()

        val isPending inline get() = this is Pending
    }
}