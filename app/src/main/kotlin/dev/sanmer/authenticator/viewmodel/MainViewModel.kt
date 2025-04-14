package dev.sanmer.authenticator.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.authenticator.datastore.model.Preference
import dev.sanmer.authenticator.repository.PreferenceRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {
    var loadState by mutableStateOf<LoadState>(LoadState.Pending)
        private set

    val isPending inline get() = loadState.isPending
    val isLocked inline get() = loadState.isLocked
    val preference inline get() = loadState.preference

    init {
        Timber.d("MainViewModel init")
        preferenceObserver()
    }

    private fun preferenceObserver() {
        viewModelScope.launch {
            preferenceRepository.data.collect {
                loadState = if (loadState.isReady || !it.isEncrypted ) {
                    LoadState.Ready(it)
                } else {
                    LoadState.Locked(it)
                }
            }
        }
    }

    fun setUnlocked() {
        loadState = LoadState.Ready(preference)
    }

    sealed class LoadState {
        abstract val preference: Preference

        data object Pending : LoadState() {
            override val preference = Preference()
        }

        data class Locked(
            override val preference: Preference
        ) : LoadState()

        data class Ready(
            override val preference: Preference
        ) : LoadState()

        val isPending inline get() = this is Pending
        val isLocked inline get() = this is Locked
        val isReady inline get() = this is Ready
    }
}