package dev.sanmer.authenticator.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.authenticator.model.auth.Auth
import dev.sanmer.authenticator.model.serializer.AuthJson
import dev.sanmer.authenticator.repository.DbRepository
import dev.sanmer.authenticator.ui.CryptoActivity
import dev.sanmer.encoding.isBase32
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dbRepository: DbRepository
) : ViewModel() {
    private var encrypted = emptyList<Auth>()

    init {
        Timber.d("SettingsViewModel init")
    }

    fun encrypt(context: Context, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val auths = dbRepository.getAuthAllAsFlow(enable = true).first()

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
                        dbRepository.insertAuth(decrypted)
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