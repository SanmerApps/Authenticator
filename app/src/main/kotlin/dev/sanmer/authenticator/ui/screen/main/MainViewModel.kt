package dev.sanmer.authenticator.ui.screen.main

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sanmer.auth.crypto.SessionKey
import dev.sanmer.auth.decodeBase64
import dev.sanmer.authenticator.Logger
import dev.sanmer.authenticator.crypto.BiometricKey.Default.decryptKeyByBiometric
import dev.sanmer.authenticator.datastore.model.Preference
import dev.sanmer.authenticator.model.LoadData
import dev.sanmer.authenticator.repository.DbRepository
import dev.sanmer.authenticator.repository.PreferenceRepository
import dev.sanmer.authenticator.repository.TimeRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainViewModel(
    private val dbRepository: DbRepository,
    private val preferenceRepository: PreferenceRepository,
    private val timeRepository: TimeRepository
) : ViewModel() {
    var preference by mutableStateOf<LoadData<Preference>>(LoadData.Pending)
        private set

    var isEncrypted by mutableStateOf(false)
        private set

    val password = TextFieldState()
    var isError by mutableStateOf(false)
        private set

    private val logger = Logger.Android("MainViewModel")

    init {
        logger.d("init")
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            preferenceRepository.data
                .onEach {
                    logger.d("$it")
                    preference = LoadData.Success(it)
                }
                .distinctUntilChanged { old, new ->
                    old.ntp == new.ntp && old.ntpAddress == new.ntpAddress
                }
                .collectLatest {
                    timeRepository.sync(it.ntpServer())
                }
        }
    }

    fun isDecrypted(preference: Preference): Boolean {
        if (!preference.isEncrypted) isEncrypted = true
        return isEncrypted
    }

    fun decryptByPassword(key: String) {
        viewModelScope.launch {
            runCatching {
                val key = SessionKey.decryptKeyByPassword(key.decodeBase64(), password.text)
                dbRepository.setSessionKey(key)
                isEncrypted = true
            }.onFailure {
                isError = true
            }
        }
    }

    fun decryptByBiometric(key: String) {
        viewModelScope.launch {
            runCatching {
                val key = SessionKey.decryptKeyByBiometric(key.decodeBase64())
                dbRepository.setSessionKey(key)
                isEncrypted = true
            }
        }
    }
}