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
import dev.sanmer.authenticator.model.serializer.AuthJson
import dev.sanmer.authenticator.model.serializer.AuthTxt
import dev.sanmer.authenticator.repository.DbRepository
import dev.sanmer.authenticator.ui.CryptoActivity
import dev.sanmer.encoding.isBase32
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dbRepository: DbRepository
) : ViewModel() {
    private var output = emptyList<Auth>()
    private var input = emptyList<Auth>()

    var bottomSheet by mutableStateOf(BottomSheet.Closed)
        private set

    init {
        Timber.d("SettingsViewModel init")
    }

    fun updateBottomSheet(block: (BottomSheet) -> BottomSheet) {
        bottomSheet = block(bottomSheet)
    }

    fun closeBottomSheet() {
        bottomSheet = BottomSheet.Closed
    }

    fun prepare(
        fileType: FileType,
        context: Context,
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val auths = dbRepository.getAuthAllAsFlow(enable = true).first()
            if (fileType.skip) {
                output = auths
                callback()
            } else {
                CryptoActivity.encrypt(
                    context = context,
                    input = auths.map { it.secret }
                ) { encryptedSecrets ->
                    output = auths.mapIndexed { index, auth ->
                        auth.copy(secret = encryptedSecrets[index])
                    }
                    callback()
                }
            }
        }
    }

    private fun importFrom(
        fileType: FileType,
        context: Context,
        uri: Uri,
        bypass: Boolean = true,
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val cr = context.contentResolver
                checkNotNull(cr.openInputStream(uri)).use(fileType::decodeFrom)
            }.onSuccess { auths ->
                if (fileType.skip) {
                    input = auths
                    callback()
                } else {
                    CryptoActivity.decrypt(
                        context = context,
                        input = auths.map { it.secret },
                        bypass = bypass
                    ) { decryptedSecrets ->
                        input = auths.mapIndexed { index, auth ->
                            auth.copy(secret = decryptedSecrets[index])
                        }
                        callback()
                    }
                }
            }.onFailure {
                Timber.e(it)
            }
        }
    }

    fun importFrom(
        fileType: FileType,
        context: Context,
        uri: Uri
    ) = importFrom(
        fileType = fileType,
        context = context,
        uri = uri
    ) {
        val ok = input.all { it.secret.isBase32() }
        if (ok) viewModelScope.launch {
            dbRepository.insertAuth(input)
        }
    }

    private fun exportTo(
        fileType: FileType,
        context: Context,
        uri: Uri,
        auths: List<Auth>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val cr = context.contentResolver
                checkNotNull(cr.openOutputStream(uri)).use { fileType.decodeTo(auths, it) }
            }.onFailure {
                Timber.e(it)
            }
        }
    }

    fun exportTo(
        fileType: FileType,
        context: Context,
        uri: Uri
    ) = exportTo(
        fileType = fileType,
        context = context,
        uri = uri,
        auths = output
    )

    fun decryptFromJson(
        context: Context,
        uri: Uri,
        callback: () -> Unit
    ) = importFrom(
        fileType = FileType.Json,
        context = context,
        uri = uri,
        bypass = false,
        callback = callback
    )

    fun decryptedToJson(
        context: Context,
        uri: Uri
    ) = exportTo(
        fileType = FileType.Json,
        context = context,
        uri = uri,
        auths = input
    )

    sealed class FileType {
        abstract val skip: Boolean
        abstract fun decodeFrom(input: InputStream): List<Auth>
        abstract fun decodeTo(auths: List<Auth>, output: OutputStream)

        data object Txt : FileType() {
            override val skip = true

            override fun decodeFrom(input: InputStream): List<Auth> {
                return AuthTxt.decodeFrom(input).auths.map { it.auth }
            }

            override fun decodeTo(auths: List<Auth>, output: OutputStream) {
                AuthTxt(auths).encodeTo(output)
            }
        }

        data object Json : FileType() {
            override val skip = false

            override fun decodeFrom(input: InputStream): List<Auth> {
                return AuthJson.decodeFrom(input).auths.map { it.auth }
            }

            override fun decodeTo(auths: List<Auth>, output: OutputStream) {
                AuthJson(auths).encodeTo(output)
            }
        }
    }

    enum class BottomSheet {
        Closed,
        Token,
        Database,
        Tool,
        Preference;

        val isClosed inline get() = this == Closed
    }
}