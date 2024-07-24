package dev.sanmer.authenticator.viewmodel

import android.net.Uri
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.authenticator.ktx.decodeOtpAuth
import dev.sanmer.authenticator.model.auth.Auth
import dev.sanmer.authenticator.model.auth.HotpAuth
import dev.sanmer.authenticator.model.auth.Otp
import dev.sanmer.authenticator.model.auth.TotpAuth
import dev.sanmer.authenticator.repository.DbRepository
import dev.sanmer.encoding.encodeBase32Default
import dev.sanmer.encoding.isBase32
import dev.sanmer.otp.HOTP
import dev.sanmer.otp.OtpUri.Companion.isOtpUri
import kotlinx.coroutines.launch
import timber.log.Timber
import java.security.SecureRandom
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val dbRepository: DbRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private var secret by mutableStateOf(savedStateHandle.secret)
    val addAccount by derivedStateOf { secret.isBlank() || secret.isOtpUri() }

    var input by mutableStateOf(Input())
        private set

    var uriString by mutableStateOf("")
        private set

    var showQr by mutableStateOf(false)
        private set

    private val checks = mutableStateMapOf<Check, Boolean>()

    init {
        Timber.d("EditViewModel init")
        updateFromUri(secret)
        authObserver()
    }

    private fun authObserver() {
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

    private fun check(): Boolean {
        Check.Name.check(input.name, checks::put)
        Check.Issuer.check(input.issuer, checks::put)
        Check.Secret.check(input.secret, checks::put)
        return checks.all { it.value }
    }

    fun isFailed(value: Check) = !checks.getOrDefault(value, true)

    fun updateFromUri(uri: String) {
        if (!uri.isOtpUri()) return

        runCatching {
            uri.decodeOtpAuth()
        }.onSuccess {
            updateFromAuth(it.auth)
        }
    }

    fun update(block: (Input) -> Input) {
        input = block(input)
    }

    fun save(block: () -> Unit = {}) {
        if (!check()) return

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

    private inline fun Check.check(value: String, block: (Check, Boolean) -> Unit) {
        block(this, ok(value))
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

        val hotp get() = HotpAuth(
            name = name,
            issuer = issuer,
            secret = secret,
            hash = hash,
            digits = digits.toIntOrNull() ?: 6,
            count = counter.toLongOrNull() ?: 0L
        )

        val totp get() = TotpAuth(
            name = name,
            issuer = issuer,
            secret = secret,
            hash = hash,
            digits = digits.toIntOrNull() ?: 6,
            period = period.toLongOrNull() ?: 30L
        )

        val auth: Auth get() = when (type) {
            Auth.Type.HOTP -> hotp
            Auth.Type.TOTP -> totp
        }
    }

    enum class Check(val ok: (String) -> Boolean) {
        Issuer(String::isNotBlank),
        Name(String::isNotBlank),
        Secret(String::isBase32)
    }

    companion object {
        private val SavedStateHandle.secret: String
            inline get() = checkNotNull(Uri.decode(get("secret")))
    }
}