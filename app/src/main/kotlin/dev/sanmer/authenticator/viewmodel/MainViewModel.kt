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

    val isPending inline get() = loadState is LoadState.Pending
    val preference inline get() = loadState.preference

    private var isUnlocked = false

    init {
        Timber.d("MainViewModel init")
        preferenceObserver()
    }

    private fun preferenceObserver() {
        viewModelScope.launch {
            preferenceRepository.data.collect {
                loadState = if (isReady(it)) {
                    LoadState.Ready(it)
                } else {
                    LoadState.Locked(it)
                }
            }
        }
    }

    private fun isReady(preference: Preference): Boolean {
        return !preference.isEncrypted || isUnlocked || loadState.isReady
    }

    fun setUnlocked() {
        isUnlocked = true
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

        val isReady inline get() = this is Ready
    }
}