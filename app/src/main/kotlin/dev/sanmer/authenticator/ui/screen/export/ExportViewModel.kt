package dev.sanmer.authenticator.ui.screen.export

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sanmer.auth.crypto.Crypto
import dev.sanmer.auth.crypto.PasswordKey
import dev.sanmer.authenticator.Const.isZero
import dev.sanmer.authenticator.Logger
import dev.sanmer.authenticator.database.model.AuthProperties
import dev.sanmer.authenticator.ktx.stateIn
import dev.sanmer.authenticator.model.LoadData
import dev.sanmer.authenticator.model.LoadData.Default.loadData
import dev.sanmer.authenticator.model.serializable.AuthSet
import dev.sanmer.authenticator.model.serializable.AuthSet.Default.toAuthSet
import dev.sanmer.authenticator.repository.DbRepository
import dev.sanmer.authenticator.repository.OtpRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExportViewModel(
    private val dbRepository: DbRepository,
    private val otpRepository: OtpRepository
) : ViewModel() {
    val input = Input()

    var source by mutableStateOf<LoadData<Source>>(LoadData.Pending)
    val isExternal by derivedStateOf {
        source.getOrElse({ it == Source.External }) { false }
    }

    val list = mutableStateListOf<Pair<AuthProperties, Result<StateFlow<String>>>>()
    val isEmpty inline get() = list.isEmpty()

    private val _selected = mutableStateListOf<AuthProperties>()

    private val logger = Logger.Android("ExportViewModel")

    init {
        logger.d("init")
    }

    fun isSelected(auth: AuthProperties) = _selected.contains(auth)

    fun pick(auth: AuthProperties) = if (isSelected(auth)) {
        _selected.remove(auth)
    } else {
        _selected.add(auth)
    }

    fun clear() {
        _selected.clear()
        source = LoadData.Pending
        list.clear()
    }

    fun import(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val cr = context.contentResolver
            val stream = cr.openInputStream(uri) ?: return@launch
            source = loadData {
                val elements = when (input.typeValue) {
                    Input.Type.Json -> {
                        val set = stream.use(AuthSet::decodeFromJson)
                        val password = input.passwordValue
                        val key = if (password.isNotEmpty()) {
                            PasswordKey.new(password)
                        } else {
                            Crypto.Default
                        }
                        set.map { auth ->
                            val auth = auth.protectValue { key.decrypt(it) }
                            auth to runCatching {
                                otpRepository.otp(auth)
                                    .stateIn(
                                        scope = viewModelScope,
                                        started = SharingStarted.Eagerly
                                    )
                            }.onSuccess {
                                _selected.add(auth)
                            }
                        }
                    }

                    Input.Type.Uri -> {
                        val set = stream.use(AuthSet::decodeFromUri)
                        set.map { auth ->
                            auth to runCatching {
                                otpRepository.otp(auth)
                                    .stateIn(
                                        scope = viewModelScope,
                                        started = SharingStarted.Eagerly
                                    )
                            }.onSuccess {
                                _selected.add(auth)
                            }
                        }
                    }
                }
                list.addAll(elements)
                Source.External
            }
        }
    }

    fun export(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val cr = context.contentResolver
            val stream = cr.openOutputStream(uri) ?: return@launch
            runCatching {
                when (input.typeValue) {
                    Input.Type.Json -> {
                        val password = input.passwordValue
                        val key = if (password.isNotEmpty()) {
                            PasswordKey.new(password)
                        } else {
                            Crypto.Default
                        }
                        val set = _selected.map { auth ->
                            auth.protectValue { key.encrypt(it) }
                        }.toAuthSet()
                        stream.use(set::encodeToJson)
                    }

                    Input.Type.Uri -> {
                        val set = _selected.toAuthSet()
                        stream.use(set::encodeToUri)
                    }
                }
            }.onFailure {
                logger.e(it)
            }
        }
    }

    fun dbImport() {
        viewModelScope.launch {
            source = loadData {
                val elements = dbRepository.getAllAuthProperties()
                    .map {
                        if (it.auth.trashedAt.isZero) _selected.add(it)
                        it to runCatching {
                            otpRepository.otp(it)
                                .stateIn(
                                    scope = viewModelScope,
                                    started = SharingStarted.Eagerly
                                )
                        }
                    }
                list.addAll(elements)
                Source.Internal
            }
        }
    }

    fun dbExport() {
        viewModelScope.launch {
            _selected.forEach { auth ->
                runCatching {
                    dbRepository.upsert(auth)
                }.onFailure {
                    logger.e(it)
                }
            }
        }
    }

    data class Input(
        val password: TextFieldState,
        val hidden: MutableState<Boolean>,
        val type: MutableState<Type>,
    ) {
        constructor(
            password: String = "",
            hidden: Boolean = true,
            type: Type = Type.Json
        ) : this(
            password = TextFieldState(password),
            hidden = mutableStateOf(hidden),
            type = mutableStateOf(type)
        )

        val passwordValue inline get() = password.text.trim()
        var typeValue by type

        enum class Type(val mimeType: String) {
            Json("application/json"),
            Uri("text/plain")
        }
    }

    enum class Source {
        Internal,
        External
    }
}