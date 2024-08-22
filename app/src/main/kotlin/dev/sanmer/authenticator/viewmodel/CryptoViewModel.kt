package dev.sanmer.authenticator.viewmodel

import android.content.Intent
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.authenticator.ui.CryptoActivity.Action
import dev.sanmer.authenticator.ui.CryptoActivity.Default.input
import dev.sanmer.authenticator.viewmodel.CryptoViewModel.State.Default.isRunning
import dev.sanmer.crypto.CryptoFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CryptoViewModel @Inject constructor() : ViewModel() {
    private var action by mutableStateOf(Action.Encrypt)

    var hidden by mutableStateOf(true)
        private set

    var data by mutableStateOf(emptyList<String>())
    var password by mutableStateOf("")
        private set

    val isSkip by derivedStateOf { password.isBlank() }
    val isEncrypt by lazy { action == Action.Encrypt }
    val isDecrypt by lazy { action == Action.Decrypt }

    var state by mutableStateOf(State.Wait)
        private set

    init {
        Timber.d("CryptoViewModel init")
    }

    private fun run() = viewModelScope.launch(Dispatchers.Default) {
        runCatching {
            val factory = CryptoFactory(password)
            state = State.Running
            data = when (action) {
                Action.Encrypt -> data.map(factory::encrypt)
                Action.Decrypt -> data.map(factory::decrypt)
            }
        }.onSuccess {
            state = State.Ok
        }.onFailure {
            state = State.Failed
        }
    }

    operator fun invoke() = when {
        state.isRunning -> false
        isSkip -> true
        else -> run().isCompleted
    }

    fun updateFromIntent(block: () -> Intent) {
        val intent = block()
        data = intent.input.toList()
        action = checkNotNull(Action.fromStr(intent.action))
    }

    fun updateHidden(block: (Boolean) -> Boolean) {
        hidden = block(hidden)
    }

    fun updatePassword(value: String) {
        password = value
    }

    fun rewind() {
        data = emptyList()
    }

    enum class State {
        Wait,
        Running,
        Failed,
        Ok;

        companion object Default {
            val State.isRunning inline get() = this == Running
            val State.isFailed inline get() = this == Failed
            val State.isOk inline get() = this == Ok
        }
    }

}