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
import dev.sanmer.qrcode.QRCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val uriFlow = MutableStateFlow("")
    val uri get() = uriFlow.asStateFlow()

    init {
        Timber.d("SettingsViewModel init")
    }

    fun rewind() {
        uriFlow.value = ""
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

    fun decryptFromJson(context: Context, uri: Uri, callback: () -> Unit) =
        importFromJson(
            context = context,
            uri = uri,
            callback = callback
        )

    fun decryptedToJson(context: Context, uri: Uri) =
        exportToJson(
            context = context,
            uri = uri,
            auths = decrypted
        )

    fun scanImage(context: Context, uri: Uri) {
        runCatching {
            val cr = context.contentResolver
            cr.openInputStream(uri).let(::requireNotNull).use(QRCode::decodeFromStream)
        }.onSuccess {
            uriFlow.value = it
        }.onFailure {
            Timber.e(it)
        }
    }
}