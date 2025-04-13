package dev.sanmer.authenticator.viewmodel

import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.authenticator.repository.PreferenceRepository
import dev.sanmer.authenticator.repository.SecureRepository
import dev.sanmer.authenticator.ui.AuthorizeActivity.Action
import dev.sanmer.authenticator.viewmodel.MainViewModel.LoadState
import dev.sanmer.crypto.BiometricKey
import dev.sanmer.crypto.BiometricKey.Default.decryptKeyByBiometric
import dev.sanmer.crypto.BiometricKey.Default.getKeyEncryptedByBiometric
import dev.sanmer.crypto.SessionKey
import dev.sanmer.encoding.decodeBase64
import dev.sanmer.encoding.encodeBase64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthorizeViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
    private val secureRepository: SecureRepository
) : ViewModel() {
    var action by mutableStateOf(Action.Auth)
        private set

    var type by mutableStateOf<Type>(Type.PasswordPending)
        private set

    var loadState by mutableStateOf<LoadState>(LoadState.Pending)
        private set
    private val preference inline get() = loadState.preference

    var isSupportedBiometric by mutableStateOf(false)
        private set

    init {
        Timber.d("AuthorizeViewModel init")
        preferenceObserver()
    }

    private fun preferenceObserver() {
        viewModelScope.launch {
            preferenceRepository.data.collect {
                loadState = LoadState.Ready(it)
                if (it.isBiometric) {
                    type = Type.BiometricPending
                    isSupportedBiometric = true
                }
            }
        }
    }

    fun updateFromIntent(getIntent: () -> Intent) {
        val intent = getIntent()
        action = Action(intent.action)
    }

    fun setupPassword(new: String) {
        viewModelScope.launch {
            runCatching {
                val sessionKey = SessionKey.new()
                val newKey = sessionKey.getKeyEncryptedByPassword(new).encodeBase64()
                preferenceRepository.setKeyEncryptedByPassword(newKey)
                secureRepository.setSessionKey(sessionKey)
                secureRepository.encryptSecret(sessionKey)
                type = Type.PasswordSucceed
            }.onFailure {
                Timber.w(it)
            }
        }
    }

    private fun checkPassword(
        current: String,
        callback: suspend CoroutineScope.(SessionKey) -> Unit
    ) {
        if (type.isPasswordFailed) type = Type.PasswordPending
        viewModelScope.launch {
            val key = preference.keyEncryptedByPassword
            runCatching {
                callback(
                    SessionKey.decryptKeyByPassword(key.decodeBase64(), current)
                )
            }.onFailure {
                type = Type.PasswordFailed
                Timber.w(it)
            }
        }
    }

    fun changePassword(current: String, new: String) = checkPassword(current) { sessionKey ->
        val newSessionKey = SessionKey.new()
        val newKey = newSessionKey.getKeyEncryptedByPassword(new).encodeBase64()
        preferenceRepository.setKeyEncryptedByPassword(newKey)
        preferenceRepository.setKeyEncryptedByBiometric("")
        secureRepository.setSessionKey(newSessionKey)
        secureRepository.encryptSecretByNewKey(sessionKey, newSessionKey)
        type = Type.PasswordSucceed
    }

    fun removePassword(current: String) = checkPassword(current) { sessionKey ->
        preferenceRepository.setKeyEncryptedByPassword("")
        preferenceRepository.setKeyEncryptedByBiometric("")
        secureRepository.setSessionKey(null)
        secureRepository.decryptSecret(sessionKey)
        type = Type.PasswordSucceed
    }

    fun setupBiometric(
        current: String,
        activity: FragmentActivity
    ) = checkPassword(current) { sessionKey ->
        runCatching {
            BiometricKey.new()
            val key = sessionKey.getKeyEncryptedByBiometric(activity).encodeBase64()
            preferenceRepository.setKeyEncryptedByBiometric(key)
            type = Type.BiometricSucceed
        }.onFailure {
            type = Type.BiometricFailed
            Timber.w(it)
        }
    }

    fun removeBiometric(
        current: String
    ) = checkPassword(current) {
        preferenceRepository.setKeyEncryptedByBiometric("")
        type = Type.PasswordSucceed
    }

    fun loadSessionKeyByPassword(current: String) {
        viewModelScope.launch {
            val key = preference.keyEncryptedByPassword
            runCatching {
                secureRepository.setSessionKey(
                    SessionKey.decryptKeyByPassword(key.decodeBase64(), current)
                )
                type = Type.PasswordSucceed
            }.onFailure {
                type = Type.PasswordFailed
                Timber.w(it)
            }
        }
    }

    fun retryBiometric() {
        type = Type.BiometricPending
    }

    fun loadSessionKeyByBiometric(activity: FragmentActivity) {
        if (action != Action.Auth) return
        viewModelScope.launch {
            if (!BiometricKey.canAuthenticate(activity)) {
                isSupportedBiometric = false
                return@launch
            }

            runCatching {
                val key = preference.keyEncryptedByBiometric.decodeBase64()
                secureRepository.setSessionKey(
                    SessionKey.decryptKeyByBiometric(key, activity)
                )
                type = Type.BiometricSucceed
            }.onFailure {
                Timber.w(it)
                type = Type.PasswordPending
            }
        }
    }

    sealed class Type {
        abstract val state: State

        data class Biometric(
            override val state: State
        ) : Type()

        data class Password(
            override val state: State
        ) : Type()

        val isPending inline get() = state == State.Pending
        val isFailed inline get() = state == State.Failed
        val isSucceed inline get() = state == State.Succeed
        val isPassword inline get() = this is Password
        val isBiometricPending inline get() = this is Biometric && isPending
        val isPasswordFailed inline get() = isPassword && isFailed

        companion object Default {
            val BiometricPending = Biometric(State.Pending)
            val BiometricSucceed = Biometric(State.Succeed)
            val BiometricFailed = Biometric(State.Failed)
            val PasswordPending = Password(State.Pending)
            val PasswordSucceed = Password(State.Succeed)
            val PasswordFailed = Password(State.Failed)
        }
    }

    enum class State {
        Pending,
        Succeed,
        Failed,
    }
}