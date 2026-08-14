package dev.sanmer.authenticator.ui.screen.setting

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sanmer.auth.crypto.SessionKey
import dev.sanmer.auth.encodeBase64
import dev.sanmer.authenticator.Logger
import dev.sanmer.authenticator.crypto.BiometricKey.Default.getKeyEncryptedByBiometric
import dev.sanmer.authenticator.repository.DbRepository
import dev.sanmer.authenticator.repository.PreferenceRepository
import kotlinx.coroutines.launch

class SettingViewModel(
    private val dbRepository: DbRepository,
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {
    var trashed by mutableIntStateOf(0)
        private set
    val isTrashNotEmpty inline get() = trashed > 0

    val password = TextFieldState()
    val hidden = mutableStateOf(true)

    var bottomSheet by mutableStateOf<BottomSheet>(BottomSheet.None)

    private val logger = Logger.Android("SettingViewModel")

    init {
        logger.d("init")
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            dbRepository.getTrashedCountAsFlow()
                .collect { trashed = it }
        }
    }

    fun setupPassword() {
        viewModelScope.launch {
            val key = SessionKey.new()
            val keyEncrypted = key.getKeyEncryptedByPassword(password.text.trim())
            preferenceRepository.setKeyEncryptedByPassword(keyEncrypted.encodeBase64())
            dbRepository.encrypt(key)
            password.clearText()
        }
    }

    fun changePassword() {
        viewModelScope.launch {
            val key = dbRepository.getSessionKey()
            if (key is SessionKey) {
                val keyEncrypted = key.getKeyEncryptedByPassword(password.text.trim())
                preferenceRepository.setKeyEncryptedByPassword(keyEncrypted.encodeBase64())
                password.clearText()
            }
        }
    }

    fun removePassword() {
        viewModelScope.launch {
            preferenceRepository.setKeyEncryptedByPassword("")
            preferenceRepository.setKeyEncryptedByBiometric("")
            dbRepository.decrypt()
        }
    }

    fun setupBiometric() {
        viewModelScope.launch {
            val key = dbRepository.getSessionKey()
            if (key is SessionKey) {
                val keyEncrypted = key.getKeyEncryptedByBiometric()
                preferenceRepository.setKeyEncryptedByBiometric(keyEncrypted.encodeBase64())
            }
        }
    }

    fun removeBiometric() {
        viewModelScope.launch {
            preferenceRepository.setKeyEncryptedByBiometric("")
        }
    }

    fun setSecureWindow(value: Boolean) {
        viewModelScope.launch {
            preferenceRepository.setSecureWindow(value)
        }
    }

    sealed interface BottomSheet {
        data object None : BottomSheet
        data object Password : BottomSheet
    }
}