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
    private var decrypted = emptyList<Auth>()

    init {
        Timber.d("SettingsViewModel init")
    }

    private fun decrypt(
        context: Context,
        auths: List<Auth>,
        callback: () -> Unit
    ) = CryptoActivity.decrypt(
        context = context,
        input = auths.map { it.secret }
    ) { decryptedSecrets ->
        decrypted = auths.mapIndexed { index, auth ->
            auth.copy(secret = decryptedSecrets[index])
        }

        callback()
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

    private fun importFromJson(context: Context, uri: Uri, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val cr = context.contentResolver
                checkNotNull(cr.openInputStream(uri)).use(AuthJson::decodeFrom)
            }.onSuccess { json ->
                decrypt(
                    context = context,
                    auths = json.auths.map { it.auth },
                    callback = callback
                )

            }.onFailure {
                Timber.e(it)
            }
        }
    }

    fun importFromJson(context: Context, uri: Uri) =
        importFromJson(
            context = context,
            uri = uri
        ) {
            val ok = decrypted.all { it.secret.isBase32() }
            if (ok) viewModelScope.launch {
                dbRepository.insertAuth(decrypted)
            }
        }

    private fun exportToJson(context: Context, uri: Uri, auths: List<Auth>) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val cr = context.contentResolver
                checkNotNull(cr.openOutputStream(uri)).use(AuthJson(auths)::encodeTo)
            }.onFailure {
                Timber.e(it)
            }
        }
    }

    fun exportToJson(context: Context, uri: Uri) =
        exportToJson(
            context = context,
            uri = uri,
            auths = encrypted
        )

    fun encryptFromJson(context: Context, uri: Uri, callback: () -> Unit) =
        importFromJson(
            context = context,
            uri = uri,
            callback = callback
        )

    fun decryptToJson(context: Context, uri: Uri) =
        exportToJson(
            context = context,
            uri = uri,
            auths = decrypted
        )
}