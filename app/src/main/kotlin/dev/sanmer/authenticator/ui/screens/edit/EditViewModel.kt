package dev.sanmer.authenticator.ui.screens.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dev.sanmer.authenticator.Logger
import dev.sanmer.authenticator.database.entity.TotpEntity
import dev.sanmer.authenticator.model.auth.AuthType
import dev.sanmer.authenticator.model.serializer.AuthUri.Default.toTotpEntity
import dev.sanmer.authenticator.model.serializer.AuthUri.Default.uri
import dev.sanmer.authenticator.repository.DbRepository
import dev.sanmer.authenticator.ui.main.Screen
import dev.sanmer.encoding.isBase32
import dev.sanmer.otp.Otp
import dev.sanmer.otp.OtpUri.Default.isOtpUri
import dev.sanmer.otp.OtpUri.Default.toOtpUri
import kotlinx.coroutines.launch

class EditViewModel(
    private val dbRepository: DbRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val edit = savedStateHandle.toRoute<Screen.Edit>()
    val isEdit = edit.id != -1L

    var input by mutableStateOf(Input())
        private set

    var uriString by mutableStateOf("")
        private set

    private val result = mutableStateMapOf<Value, Boolean>()

    private val logger = Logger.Android("EditViewModel")

    init {
        logger.d("init")
        updateFromUri(edit.uri)
        dataObserver()
    }

    private fun dataObserver() {
        viewModelScope.launch {
            dbRepository.getTotpDecryptedByIdAsFlow(edit.id)
                .collect { entity ->
                    update { Input(entity) }
                    uriString = entity.uri().toString()
                }
        }
    }

    private fun isAllOk(): Boolean {
        Value.Name.ok(input.name, result::put)
        Value.Issuer.ok(input.issuer, result::put)
        Value.Secret.ok(input.secret, result::put)
        return result.all { it.value }
    }

    fun isError(value: Value) = !(result[value] ?: true)

    private fun updateFromUri(uriString: String) {
        if (!uriString.isOtpUri()) return
        runCatching {
            uriString.toOtpUri().toTotpEntity()
        }.onSuccess { entity ->
            update { Input(entity) }
        }.onFailure {
            logger.e(it)
        }
    }

    fun update(block: (Input) -> Input) {
        input = block(input)
    }

    fun save(block: () -> Unit = {}) {
        if (isAllOk()) {
            viewModelScope.launch {
                when {
                    isEdit -> dbRepository.updateTotp(input.entity(edit.id))
                    else -> dbRepository.insertTotp(input.entity())
                }
                block()
            }
        }
    }

    data class Input(
        val type: AuthType = AuthType.TOTP,
        val name: String = "",
        val issuer: String = "",
        val secret: String = "",
        val hash: Otp.Hash = Otp.Hash.SHA1,
        val digits: String = "6",
        val period: String = "30"
    ) {
        constructor(entity: TotpEntity) : this(
            name = entity.name,
            issuer = entity.issuer,
            secret = entity.secret,
            hash = entity.hash,
            digits = entity.digits.toString(),
            period = entity.period.toString()
        )

        fun entity(id: Long = 0) = TotpEntity(
            id = id,
            name = name.trim(),
            issuer = issuer.trim(),
            secret = secret.replace("\\s+".toRegex(), ""),
            hash = hash,
            digits = digits.toInt(),
            period = period.toLong()
        )
    }

    enum class Value(val ok: (String) -> Boolean) {
        Issuer(String::isNotBlank),
        Name(String::isNotBlank),
        Secret(String::isBase32)
    }

    private inline fun Value.ok(value: String, block: (Value, Boolean) -> Unit) {
        block(this, ok(value))
    }
}