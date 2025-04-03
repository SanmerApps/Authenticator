package dev.sanmer.authenticator.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.authenticator.model.auth.Auth
import dev.sanmer.authenticator.model.auth.HotpAuth
import dev.sanmer.authenticator.model.auth.Otp
import dev.sanmer.authenticator.model.auth.TotpAuth
import dev.sanmer.authenticator.model.serializer.AuthTxt
import dev.sanmer.authenticator.repository.DbRepository
import dev.sanmer.encoding.encodeBase32Default
import dev.sanmer.encoding.isBase32
import dev.sanmer.otp.HOTP
import dev.sanmer.otp.OtpUri.Default.isOtpUri
import kotlinx.coroutines.launch
import timber.log.Timber
import java.security.SecureRandom
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val dbRepository: DbRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val secret = savedStateHandle.secret
    val isOtpUri = secret.isOtpUri()
    val edit = secret.isNotBlank() && !isOtpUri

    var input by mutableStateOf(Input())
        private set

    var uriString by mutableStateOf("")
        private set

    var showQr by mutableStateOf(false)
        private set

    private val result = mutableStateMapOf<Value, Boolean>()

    init {
        Timber.d("EditViewModel init")
        updateFromUri(secret)
        dataObserver()
    }

    private fun dataObserver() {
        viewModelScope.launch {
            dbRepository.getAuthBySecretAsFlow(secret)
                .collect { auth ->
                    updateFromAuth(auth)
                    updateUriString(auth)
                }
        }
    }

    private fun updateUriString(auth: Auth) {
        when (auth) {
            is Otp -> uriString = auth.uri.toString()
        }
    }

    private fun updateFromAuth(auth: Auth) {
        when (auth) {
            is HotpAuth -> update { Input(auth) }
            is TotpAuth -> update { Input(auth) }
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
        }.onSuccess {
            updateFromAuth(it.auth)
        }.onFailure {
            Timber.e(it, "uri = $uriString")
        }
    }

    fun update(block: (Input) -> Input) {
        input = block(input)
    }

    fun save(block: () -> Unit = {}) {
        if (!isAllOk()) return

        viewModelScope.launch {
            dbRepository.updateAuth(input.auth)
            block()
        }
    }

    fun updateShowQr(block: (Boolean) -> Boolean) {
        showQr = block(showQr)
    }

    fun randomSecret() {
        val secret = ByteArray(32).apply {
            SecureRandom().nextBytes(this)
        }.encodeBase32Default()

        update { it.copy(secret = secret) }
    }

    data class Input(
        val type: Auth.Type = Auth.Type.TOTP,
        val name: String = "",
        val issuer: String = "",
        val secret: String = "",
        val hash: HOTP.Hash = HOTP.Hash.SHA1,
        val digits: String = "6",
        val counter: String = "0",
        val period: String = "30"
    ) {
        constructor(hotp: HotpAuth) : this(
            type = Auth.Type.HOTP,
            name = hotp.name,
            issuer = hotp.issuer,
            secret = hotp.secret,
            hash = hotp.hash,
            digits = hotp.digits.toString(),
            counter = hotp.counter.toString()
        )

        constructor(totp: TotpAuth) : this(
            type = Auth.Type.TOTP,
            name = totp.name,
            issuer = totp.issuer,
            secret = totp.secret,
            hash = totp.hash,
            digits = totp.digits.toString(),
            period = totp.period.toString()
        )

        val auth: Auth get() = when (type) {
            Auth.Type.HOTP -> HotpAuth(
                name = name.trim(),
                issuer = issuer.trim(),
                secret = secret.replace("\\s+".toRegex(), ""),
                hash = hash,
                digits = digits.toIntOrNull() ?: 6,
                count = counter.toLongOrNull() ?: 0
            )
            Auth.Type.TOTP -> TotpAuth(
                name = name.trim(),
                issuer = issuer.trim(),
                secret = secret.replace("\\s+".toRegex(), ""),
                hash = hash,
                digits = digits.toIntOrNull() ?: 6,
                period = period.toLongOrNull() ?: 30
            )
        }
    }

    enum class Value(val ok: (String) -> Boolean) {
        Issuer(String::isNotBlank),
        Name(String::isNotBlank),
        Secret(String::isBase32)
    }

    private inline fun Value.ok(value: String, block: (Value, Boolean) -> Unit) {
        block(this, ok(value))
    }

    companion object Default {
        private val SavedStateHandle.secret: String
            inline get() = checkNotNull(Uri.decode(get("secret")))
    }
}