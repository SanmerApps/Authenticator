package dev.sanmer.authenticator.ui.screens.settings

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sanmer.authenticator.Logger
import dev.sanmer.authenticator.model.serializer.AuthJson
import dev.sanmer.authenticator.model.serializer.AuthTxt
import dev.sanmer.authenticator.model.serializer.TotpAuth
import dev.sanmer.authenticator.repository.DbRepository
import dev.sanmer.authenticator.ui.CryptoActivity
import dev.sanmer.encoding.isBase32
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream

class SettingsViewModel(
    private val dbRepository: DbRepository
) : ViewModel() {
    private var output = emptyList<TotpAuth>()
    private var input = emptyList<TotpAuth>()

    private var totp by mutableStateOf(emptyList<TotpAuth>())
    val isEmpty get() = totp.isEmpty()

    var bottomSheet by mutableStateOf(BottomSheet.Closed)
        private set

    private val logger = Logger.Android("SettingsViewModel")

    init {
        logger.d("init")
        dataObserver()
    }

    private fun dataObserver() {
        viewModelScope.launch {
            dbRepository.getTotpAllDecryptedAsFlow()
                .collect { entries ->
                    totp = entries.map { it.totp }
                }
        }
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
            if (fileType.bypass) {
                output = totp
                callback()
            } else {
                CryptoActivity.encrypt(
                    context = context,
                    input = totp.map { it.secret }
                ) { encryptedSecrets ->
                    output = totp.mapIndexed { index, auth ->
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
                if (fileType.bypass) {
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
                logger.e(it)
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
        viewModelScope.launch {
            dbRepository.insertTotp(
                input.filter { it.secret.isBase32() }
            )
        }
    }

    private fun exportTo(
        fileType: FileType,
        context: Context,
        uri: Uri,
        auths: List<TotpAuth>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val cr = context.contentResolver
                checkNotNull(cr.openOutputStream(uri)).use { fileType.decodeTo(auths, it) }
            }.onFailure {
                logger.e(it)
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
        abstract val bypass: Boolean
        abstract fun decodeFrom(input: InputStream): List<TotpAuth>
        abstract fun decodeTo(auths: List<TotpAuth>, output: OutputStream)

        data object Txt : FileType() {
            override val bypass = true

            override fun decodeFrom(input: InputStream): List<TotpAuth> {
                return AuthTxt.decodeFrom(input).totp
            }

            override fun decodeTo(auths: List<TotpAuth>, output: OutputStream) {
                AuthTxt(auths).encodeTo(output)
            }
        }

        data object Json : FileType() {
            override val bypass = false

            override fun decodeFrom(input: InputStream): List<TotpAuth> {
                return AuthJson.decodeFrom(input).totp
            }

            override fun decodeTo(auths: List<TotpAuth>, output: OutputStream) {
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