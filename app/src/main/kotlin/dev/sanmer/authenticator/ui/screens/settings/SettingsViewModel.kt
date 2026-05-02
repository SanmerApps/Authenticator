package dev.sanmer.authenticator.ui.screens.settings

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sanmer.authenticator.Logger
import dev.sanmer.authenticator.database.entity.TotpEntity
import dev.sanmer.authenticator.model.serializer.AuthJson
import dev.sanmer.authenticator.model.serializer.AuthUri
import dev.sanmer.authenticator.repository.DbRepository
import dev.sanmer.authenticator.ui.CryptoActivity
import dev.sanmer.encoding.isBase32
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

class SettingsViewModel(
    private val dbRepository: DbRepository
) : ViewModel() {
    private var entities by mutableStateOf(emptyList<TotpEntity>())
    val isEmpty get() = entities.isEmpty()

    private var tmp = emptyList<TotpEntity>()

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
                .collect { list ->
                    entities = list
                }
        }
    }

    fun updateBottomSheet(block: (BottomSheet) -> BottomSheet) {
        bottomSheet = block(bottomSheet)
    }

    fun closeBottomSheet() {
        bottomSheet = BottomSheet.Closed
    }

    private suspend fun import(
        fileType: FileType,
        context: Context,
        uri: Uri,
        bypass: Boolean,
    ) = withContext(Dispatchers.IO) {
        runCatching {
            val cr = context.contentResolver
            val entities = checkNotNull(cr.openInputStream(uri)).use(fileType::decodeFrom)
            when (fileType) {
                FileType.Json -> {
                    val secrets = CryptoActivity.decrypt(
                        context = context,
                        input = entities.map { it.secret },
                        bypass = bypass
                    )
                    entities.mapIndexed { index, it ->
                        it.copy(secret = secrets[index])
                    }
                }

                FileType.Uri -> entities
            }
        }.onFailure {
            logger.e(it)
        }
    }

    private fun import(
        fileType: FileType,
        context: Context,
        uri: Uri
    ) {
        viewModelScope.launch {
            import(
                fileType = fileType,
                context = context,
                uri = uri,
                bypass = true
            ).onSuccess { entities ->
                dbRepository.insertTotp(
                    entities.filter { it.secret.isBase32() }
                )
            }
        }
    }

    fun importJson(
        context: Context,
        uri: Uri
    ) = import(
        fileType = FileType.Json,
        context = context,
        uri
    )

    fun importUri(
        context: Context,
        uri: Uri
    ) = import(
        fileType = FileType.Uri,
        context = context,
        uri
    )

    private suspend fun export(
        entities: List<TotpEntity>,
        fileType: FileType,
        context: Context,
        uri: Uri,
    ) = withContext(Dispatchers.IO) {
        runCatching {
            val cr = context.contentResolver
            checkNotNull(cr.openOutputStream(uri)).use { fileType.decodeTo(entities, it) }
        }.onFailure {
            logger.e(it)
        }
    }

    fun encrypt(
        context: Context,
        onReady: () -> Unit
    ) {
        viewModelScope.launch {
            runCatching {
                val secrets = CryptoActivity.encrypt(
                    context = context,
                    input = entities.map { it.secret }
                )
                tmp = entities.mapIndexed { index, it ->
                    it.copy(secret = secrets[index])
                }
            }.onSuccess {
                onReady()
            }
        }
    }

    private fun export(
        fileType: FileType,
        context: Context,
        uri: Uri
    ) {
        viewModelScope.launch {
            export(
                entities = tmp,
                fileType = fileType,
                context = context,
                uri = uri,
            )
            tmp = emptyList()
        }
    }

    fun exportJson(
        context: Context,
        uri: Uri
    ) = export(
        fileType = FileType.Json,
        context = context,
        uri = uri
    )

    fun exportUri(
        context: Context,
        uri: Uri
    ) = export(
        fileType = FileType.Uri,
        context = context,
        uri = uri
    )

    fun decryptFromJson(
        context: Context,
        uri: Uri,
        onReady: () -> Unit
    ) {
        viewModelScope.launch {
            import(
                fileType = FileType.Json,
                context = context,
                uri = uri,
                bypass = false,
            ).onSuccess { entities ->
                tmp = entities
                onReady()
            }
        }
    }

    fun decryptedToJson(
        context: Context,
        uri: Uri,
    ) {
        viewModelScope.launch {
            export(
                entities = tmp,
                fileType = FileType.Json,
                context = context,
                uri = uri,
            )
            tmp = emptyList()
        }
    }

    private sealed interface FileType {
        fun decodeFrom(input: InputStream): List<TotpEntity>
        fun decodeTo(auths: List<TotpEntity>, output: OutputStream)

        data object Uri : FileType {
            override fun decodeFrom(input: InputStream): List<TotpEntity> {
                return AuthUri.decodeFrom(input).totp
            }

            override fun decodeTo(auths: List<TotpEntity>, output: OutputStream) {
                AuthUri(auths).encodeTo(output)
            }
        }

        data object Json : FileType {
            override fun decodeFrom(input: InputStream): List<TotpEntity> {
                return AuthJson.decodeFrom(input).entities()
            }

            override fun decodeTo(auths: List<TotpEntity>, output: OutputStream) {
                AuthJson.entities(auths).encodeTo(output)
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