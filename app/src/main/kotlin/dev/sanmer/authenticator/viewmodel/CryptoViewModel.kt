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
import dev.sanmer.authenticator.ui.CryptoActivity.Default.bypass
import dev.sanmer.authenticator.ui.CryptoActivity.Default.input
import dev.sanmer.crypto.PasswordKey
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CryptoViewModel @Inject constructor() : ViewModel() {
    private var data = emptyList<String>()
    private var action by mutableStateOf(Action.Encrypt)
    private var bypass = true

    var state by mutableStateOf(State.Pending)
        private set

    var password by mutableStateOf("")
        private set

    val isSkip by derivedStateOf { password.isEmpty() && bypass }
    val isEncrypt by lazy { action == Action.Encrypt }
    val isDecrypt by lazy { action == Action.Decrypt }

    val output get() = if (state.isSucceed) data else emptyList()

    init {
        Timber.d("TextCryptoViewModel init")
    }

    fun crypto() {
        if (state.isRunning) return
        if (isSkip) {
            state = State.Succeed
            return
        }

        viewModelScope.launch {
            runCatching {
                val key = PasswordKey.new(password)
                state = State.Running
                data = when (action) {
                    Action.Encrypt -> data.map { key.encrypt(it) }
                    Action.Decrypt -> data.map { key.decrypt(it) }
                }
                state = State.Succeed
            }.onFailure {
                state = State.Failed
            }
        }
    }

    fun updateFromIntent(getIntent: () -> Intent) {
        val intent = getIntent()
        action = Action(intent.action)
        bypass = intent.bypass
        data = intent.input.toList()
    }

    fun updatePassword(value: String) {
        password = value
    }

    enum class State {
        Pending,
        Running,
        Succeed,
        Failed;

        val isRunning inline get() = this == Running
        val isFailed inline get() = this == Failed
        val isSucceed inline get() = this == Succeed
    }

}