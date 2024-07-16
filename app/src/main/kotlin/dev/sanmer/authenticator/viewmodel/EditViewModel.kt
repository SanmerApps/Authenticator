package dev.sanmer.authenticator.viewmodel

import android.net.Uri
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.authenticator.ktx.toIntOr
import dev.sanmer.authenticator.ktx.toLongOr
import dev.sanmer.authenticator.model.auth.Auth
import dev.sanmer.authenticator.model.auth.HotpAuth
import dev.sanmer.authenticator.model.auth.Otp
import dev.sanmer.authenticator.model.auth.TotpAuth
import dev.sanmer.authenticator.model.serializer.OtpSerializable.Companion.decodeOtpAuth
import dev.sanmer.authenticator.repository.DbRepository
import dev.sanmer.authenticator.viewmodel.EditViewModel.Check.Companion.check
import dev.sanmer.encoding.isBase32
import dev.sanmer.otp.HOTP
import dev.sanmer.otp.OtpUri.Companion.isOtpAuthUri
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val dbRepository: DbRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private var secret by mutableStateOf(savedStateHandle.secret)
    val addAccount by derivedStateOf { secret.isBlank() || secret.isOtpAuthUri() }

    var input by mutableStateOf(Input())
        private set

    var uriString by mutableStateOf("")
        private set

    var showQrCode by mutableStateOf(false)
        private set

    private val checkFailed = mutableStateListOf<Check>()

    init {
        Timber.d("EditViewModel init")
        decodeFromUri(secret)
        authObserver()
    }

    private fun authObserver() {
        viewModelScope.launch {
            dbRepository.getBySecretAsFlow(secret)
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
        checkFailed.clear()

        Check.Name.check(input.name, checkFailed::add)
        Check.Issuer.check(input.issuer, checkFailed::add)
        Check.Secret.check(input.secret, checkFailed::add)

        return checkFailed.isEmpty()
    }

    fun decodeFromUri(uri: String) {
        if (!uri.isOtpAuthUri()) return

        runCatching {
            uri.decodeOtpAuth()
        }.onSuccess {
            updateFromAuth(it.auth)
        }
    }

    fun update(block: (Input) -> Input) {
        input = block(input)
    }

    fun isFailed(value: Check) = checkFailed.contains(value)

    fun save(block: () -> Unit = {}) {
        if (!check()) return

        viewModelScope.launch {
            dbRepository.update(input.auth)
            block()
        }
    }

    fun delete(block: () -> Unit = {}) {
        if (addAccount) return

        viewModelScope.launch {
            dbRepository.delete(input.auth)
            block()
        }
    }

    fun updateShowQrCode(block: (Boolean) -> Boolean) {
        showQrCode = block(showQrCode)
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

        val hotp
            get() = HotpAuth(
                name = name,
                issuer = issuer,
                secret = secret,
                hash = hash,
                digits = digits.toIntOr(6),
                count = counter.toLongOr(0L)
            )

        val totp
            get() = TotpAuth(
                name = name,
                issuer = issuer,
                secret = secret,
                hash = hash,
                digits = digits.toIntOr(6),
                period = period.toLongOr(30L)
            )

        val auth: Auth
            get() = when (type) {
                Auth.Type.HOTP -> hotp
                Auth.Type.TOTP -> totp
            }
    }

    enum class Check(val ok: (String) -> Boolean) {
        Issuer(String::isNotBlank),
        Name(String::isNotBlank),
        Secret(String::isBase32);

        companion object {
            fun Check.check(value: String, failed: (Check) -> Unit) {
                if (!ok(value)) failed(this)
            }
        }
    }

    companion object {
        private val SavedStateHandle.secret: String
            get() =
                checkNotNull(Uri.decode(get("secret")))
    }
}