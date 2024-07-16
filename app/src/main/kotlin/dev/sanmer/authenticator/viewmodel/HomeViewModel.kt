package dev.sanmer.authenticator.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.authenticator.model.auth.Auth
import dev.sanmer.authenticator.model.auth.HotpAuth
import dev.sanmer.authenticator.model.serializer.AuthJson
import dev.sanmer.authenticator.repository.DbRepository
import dev.sanmer.authenticator.ui.CryptoActivity
import dev.sanmer.encoding.isBase32
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dbRepository: DbRepository
) : ViewModel() {
    private val authsFlow = MutableStateFlow(emptyList<Auth>())
    val auths get() = authsFlow.asStateFlow()

    var isEditing by mutableStateOf(false)
        private set

    var isSearch by mutableStateOf(false)
        private set
    private val keyFlow = MutableStateFlow("")

    private var encrypted = emptyList<Auth>()

    init {
        Timber.d("HomeViewModel init")
        dataObserver()
    }

    private fun dataObserver() {
        combine(
            dbRepository.authsFlow,
            keyFlow
        ) { source, key ->
            authsFlow.update {
                source.filter {
                    if (key.isNotBlank()) {
                        it.name.contains(key, ignoreCase = true)
                                || it.issuer.contains(key, ignoreCase = true)
                    } else {
                        true
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun search(key: String) {
        keyFlow.update { key }
    }

    fun openSearch() {
        isSearch = true
    }

    fun closeSearch() {
        isSearch = false
        keyFlow.update { "" }
    }

    fun updateHotp(auth: HotpAuth) {
        viewModelScope.launch {
            dbRepository.updateHotp(auth)
        }
    }

    fun updateEditing(block: (Boolean) -> Boolean) {
        isEditing = block(isEditing)
    }

    fun encrypt(context: Context, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val auths = authsFlow.first()

            CryptoActivity.encrypt(
                context = context,
                input = auths.map { it.secret }
            ) { encryptedSecrets ->
                encrypted = auths.mapIndexed { index, auth ->
                    auth.copy(secret = encryptedSecrets[index])
                }

                callback()
            }
        }
    }

    private fun decrypt(
        context: Context,
        auths: List<Auth>,
        callback: (List<Auth>) -> Unit
    ) = CryptoActivity.decrypt(
        context = context,
        input = auths.map { it.secret }
    ) { decryptedSecrets ->
        val decrypted = auths.mapIndexed { index, auth ->
            auth.copy(secret = decryptedSecrets[index])
        }

        callback(decrypted)
    }

    fun importFromJson(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val cr = context.contentResolver
                checkNotNull(cr.openInputStream(uri)).use(AuthJson::decodeFrom)
            }.onSuccess { json ->
                decrypt(
                    context = context,
                    auths = json.auths.map { it.auth }
                ) { decrypted ->
                    val ok = decrypted.all { it.secret.isBase32() }
                    if (ok) viewModelScope.launch {
                        dbRepository.insert(decrypted)
                    }
                }

            }.onFailure {
                Timber.e(it)
            }
        }
    }

    fun exportToJson(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val cr = context.contentResolver
                checkNotNull(cr.openOutputStream(uri)).use(AuthJson(encrypted)::encodeTo)
            }.onFailure {
                Timber.e(it)
            }

            encrypted = emptyList()
        }
    }
}