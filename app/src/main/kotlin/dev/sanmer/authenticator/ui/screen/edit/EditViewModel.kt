package dev.sanmer.authenticator.ui.screen.edit

import android.net.Uri
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sanmer.auth.Otp
import dev.sanmer.auth.OtpUri.Default.isOtpUri
import dev.sanmer.auth.OtpUri.Default.toOtpUri
import dev.sanmer.auth.QRCode
import dev.sanmer.authenticator.Const.INSTANT_ZERO
import dev.sanmer.authenticator.Const.isZero
import dev.sanmer.authenticator.Logger
import dev.sanmer.authenticator.database.model.Auth
import dev.sanmer.authenticator.database.model.AuthProperties
import dev.sanmer.authenticator.database.model.AuthProperty
import dev.sanmer.authenticator.ktx.stateIn
import dev.sanmer.authenticator.model.otp.Totp
import dev.sanmer.authenticator.repository.DbRepository
import dev.sanmer.authenticator.repository.OtpRepository
import dev.sanmer.authenticator.repository.TimeRepository
import dev.sanmer.brand.Brand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditViewModel(
    private val authId: Long,
    private val otpUri: Uri,
    private val dbRepository: DbRepository,
    private val otpRepository: OtpRepository,
    private val timeRepository: TimeRepository
) : ViewModel() {
    val isEdit = authId > 0

    val input = Input()
    var isTrashed by mutableStateOf(false)
        private set

    var brand by mutableStateOf<Brand?>(null)
        private set

    var bottomSheet by mutableStateOf<BottomSheet>(BottomSheet.None)

    private val logger = Logger.Android("EditViewModel")

    init {
        logger.d("init")
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            if (isEdit) {
                dbRepository.getAuthPropertiesAsFlow(authId)
                    .collect {
                        input.update(it)
                        isTrashed = !it.auth.trashedAt.isZero
                    }
            } else {
                fromOtpUri(otpUri)
            }
        }
    }

    private fun fromOtpUri(uri: Uri) {
        if (!uri.isOtpUri()) return
        runCatching {
            val otpUri = uri.toOtpUri()
            if (otpUri.type.equals("totp", ignoreCase = true)) {
                input.update(Totp(otpUri).toAuth())
            }
        }.onFailure {
            logger.e(it)
        }
    }

    fun fromScan(uri: Uri) {
        fromOtpUri(uri)
        bottomSheet = BottomSheet.None
    }

    fun matchesBrand() {
        viewModelScope.launch {
            brand = Brand.matches(input.issuerValue)
            brand?.let { input.issuer.setTextAndPlaceCursorAtEnd(it.name) }
        }
    }

    fun preview() {
        viewModelScope.launch {
            val preview = runCatching {
                val authId = authId.coerceAtLeast(0)
                when (input.typeValue) {
                    Auth.Type.TOTP -> {
                        val totp = input.totp()
                        val auth = totp.toAuth(authId)
                        auth to otpRepository.totp(totp)
                            .stateIn(
                                scope = viewModelScope,
                                started = SharingStarted.Eagerly
                            )
                    }
                }
            }.onFailure {
                logger.e(it)
            }
            bottomSheet = BottomSheet.Preview(preview)
        }
    }

    fun save(auth: AuthProperties, onBack: () -> Unit = {}) {
        viewModelScope.launch {
            runCatching {
                dbRepository.upsert(auth)
                if (!isEdit) onBack()
            }.onFailure {
                logger.e(it)
            }
        }
    }

    fun trash() {
        viewModelScope.launch {
            runCatching {
                runCatching {
                    dbRepository.trash(authId, timeRepository.now())
                }.onFailure {
                    logger.e(it)
                }
            }
        }
    }

    fun restore() {
        viewModelScope.launch {
            runCatching {
                dbRepository.trash(authId, INSTANT_ZERO)
            }.onFailure {
                logger.e(it)
            }
        }
    }

    fun delete(onBack: () -> Unit = {}) {
        viewModelScope.launch {
            runCatching {
                dbRepository.delete(authId)
                onBack()
            }.onFailure {
                logger.e(it)
            }
        }
    }

    fun qrcode(density: Density, color: Color) {
        viewModelScope.launch(Dispatchers.IO) {
            val qrcode = runCatching {
                val uri = when (input.typeValue) {
                    Auth.Type.TOTP -> input.totp().toUri()
                }
                val content = uri.toString()
                content to QRCode.encodeToBitmap(
                    content = content,
                    size = with(density) { 360.dp.roundToPx() },
                    foregroundColor = color.toArgb(),
                    backgroundColor = Color.Transparent.toArgb()
                ).asImageBitmap()
            }.onFailure {
                logger.e(it)
            }
            bottomSheet = BottomSheet.Qrcode(qrcode)
        }
    }

    data class Input(
        val name: TextFieldState,
        val issuer: TextFieldState,
        val secret: TextFieldState,
        val hidden: MutableState<Boolean>,
        val type: MutableState<Auth.Type>,
        val hash: MutableState<Otp.Hash>,
        val digits: TextFieldState,
        val period: TextFieldState
    ) {
        constructor(
            name: String = "",
            issuer: String = "",
            secret: String = "",
            hidden: Boolean = true,
            type: Auth.Type = Auth.Type.TOTP,
            hash: Otp.Hash = Otp.Hash.SHA1,
            digits: Int = 6,
            period: Long = 30
        ) : this(
            hidden = mutableStateOf(hidden),
            name = TextFieldState(name),
            issuer = TextFieldState(issuer),
            secret = TextFieldState(secret),
            type = mutableStateOf(type),
            hash = mutableStateOf(hash),
            digits = TextFieldState(digits.toString()),
            period = TextFieldState(period.toString())
        )

        val nameValue inline get() = name.text.trim().toString()
        val issuerValue inline get() = issuer.text.trim().toString()
        val secretValue inline get() = secret.text.trim().toString()
        var typeValue by type
        var hashValue by hash
        val digitsValue inline get() = digits.text.toString().toInt()
        val periodValue inline get() = period.text.toString().toLong()

        val isNotEmpty by derivedStateOf {
            secret.text.isNotEmpty() && digits.text.isNotEmpty() && period.text.isNotEmpty()
        }

        fun update(auth: AuthProperties) {
            name.setTextAndPlaceCursorAtEnd(
                auth.auth.name
            )
            issuer.setTextAndPlaceCursorAtEnd(
                auth.auth.issuer
            )
            secret.setTextAndPlaceCursorAtEnd(
                auth.getValue(AuthProperty.Key.Secret, "") { it }
            )
            typeValue = auth.auth.type
            hashValue = auth.getValue(AuthProperty.Key.Hash, Otp.Hash.SHA1, Otp.Hash::valueOf)
            digits.setTextAndPlaceCursorAtEnd(
                auth.getValue(AuthProperty.Key.Digits, "6") { it }
            )
            period.setTextAndPlaceCursorAtEnd(
                auth.getValue(AuthProperty.Key.Period, "30") { it }
            )
        }

        fun totp() = Totp(
            name = nameValue,
            issuer = issuerValue,
            secret = secretValue,
            hash = hashValue,
            digits = digitsValue.coerceAtLeast(1),
            period = periodValue.coerceAtLeast(1)
        )
    }

    sealed interface BottomSheet {
        data object None : BottomSheet
        data object Scan : BottomSheet
        data class Preview(val preview: Result<Pair<AuthProperties, StateFlow<String>>>) :
            BottomSheet

        data class Qrcode(val qrcode: Result<Pair<String, ImageBitmap>>) : BottomSheet
    }
}