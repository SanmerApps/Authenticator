package dev.sanmer.authenticator.viewmodel

import android.content.Intent
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.authenticator.ui.TextCryptoActivity.Action
import dev.sanmer.authenticator.ui.TextCryptoActivity.Default.allowSkip
import dev.sanmer.authenticator.ui.TextCryptoActivity.Default.input
import dev.sanmer.crypto.PasswordKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TextCryptoViewModel @Inject constructor() : ViewModel() {
    private var action by mutableStateOf(Action.Encrypt)
    private var allowSkip = true

    var state by mutableStateOf(State.Wait)
        private set

    var hidden by mutableStateOf(true)
        private set

    var data by mutableStateOf(emptyList<String>())
    var password by mutableStateOf("")
        private set

    val isSkip by derivedStateOf { password.isEmpty() && allowSkip }
    val isEncrypt by lazy { action == Action.Encrypt }
    val isDecrypt by lazy { action == Action.Decrypt }

    init {
        Timber.d("TextCryptoViewModel init")
    }

    private fun run() = viewModelScope.launch(Dispatchers.Default) {
        runCatching {
            val key = PasswordKey.new(password)
            state = State.Running
            data = when (action) {
                Action.Encrypt -> data.map { key.encrypt(it) }
                Action.Decrypt -> data.map { key.decrypt(it) }
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

    fun updateFromIntent(getIntent: () -> Intent) {
        val intent = getIntent()
        allowSkip = intent.allowSkip
        data = intent.input.toList()
        action = Action(intent.action)
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

        val isRunning inline get() = this == Running
        val isFailed inline get() = this == Failed
        val isOk inline get() = this == Ok
    }

}