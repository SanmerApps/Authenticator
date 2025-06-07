package dev.sanmer.authenticator.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.authenticator.model.AuthType
import dev.sanmer.authenticator.model.serializer.AuthTxt
import dev.sanmer.authenticator.model.serializer.TotpAuth
import dev.sanmer.authenticator.repository.DbRepository
import dev.sanmer.authenticator.ui.main.Screen
import dev.sanmer.encoding.isBase32
import dev.sanmer.otp.HOTP
import dev.sanmer.otp.OtpUri.Default.isOtpUri
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
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

    init {
        Timber.d("EditViewModel init")
        updateFromUri(edit.uri)
        dataObserver()
    }

    private fun dataObserver() {
        viewModelScope.launch {
            dbRepository.getTotpDecryptedByIdAsFlow(edit.id)
                .map { it.totp }
                .collect { totp ->
                    update { Input(totp) }
                    uriString = totp.uri.toString()
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
            AuthTxt.parse(uriString)
        }.onSuccess { totp ->
            update { Input(totp) }
        }.onFailure {
            Timber.e(it, "uri = $uriString")
        }
    }

    fun update(block: (Input) -> Input) {
        input = block(input)
    }

    fun save(block: () -> Unit = {}) {
        if (isAllOk()) {
            viewModelScope.launch {
                when {
                    isEdit -> dbRepository.updateTotp(edit.id, input.auth)
                    else -> dbRepository.insertTotp(input.auth)
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
        val hash: HOTP.Hash = HOTP.Hash.SHA1,
        val digits: String = "6",
        val period: String = "30"
    ) {
        constructor(auth: TotpAuth) : this(
            name = auth.name,
            issuer = auth.issuer,
            secret = auth.secret,
            hash = auth.hash,
            digits = auth.digits.toString(),
            period = auth.period.toString()
        )

        val auth inline get() = TotpAuth(
            name = name.trim(),
            issuer = issuer.trim(),
            secret = secret.replace("\\s+".toRegex(), ""),
            hash = hash,
            digits = digits.toIntOrNull() ?: 6,
            period = period.toLongOrNull() ?: 30
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