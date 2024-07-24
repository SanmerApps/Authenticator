package dev.sanmer.authenticator.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.encoding.decodeBase32
import dev.sanmer.encoding.decodeBase64
import dev.sanmer.encoding.encodeBase32Default
import dev.sanmer.encoding.encodeBase64
import dev.sanmer.encoding.isBase32
import dev.sanmer.encoding.isBase64
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EncodeViewModel @Inject constructor() : ViewModel() {
    var encoded by mutableStateOf("")
        private set

    var decoded by mutableStateOf("")
        private set

    var type by mutableStateOf(Type.Base64)
        private set

    private val errors = mutableStateMapOf<Error, Boolean>()

    init {
        Timber.d("EncodeViewModel init")
    }

    fun updateEncoded(value: String) {
        if (value != encoded) encoded = value
    }

    fun updateDecoded(value: String) {
        if (value != decoded) decoded = value
    }

    fun updateType(value: Type) {
        type = value
    }

    fun isError(value: Error) =
        errors.getOrDefault(value, false)

    fun encode() {
        errors[Error.Decode] = false
        errors[Error.Encode] = runCatching {
            encoded = type.encode(decoded)
        }.isFailure
    }

    fun decode() {
        errors[Error.Encode] = false
        errors[Error.Decode] = runCatching {
            decoded = type.decode(encoded)
        }.isFailure
    }

    enum class Type(
        val ok: (String) -> Boolean,
        val encode: (String) -> String,
        val decode: (String) -> String
    ) {
        Base32(
            ok = String::isBase32,
            encode = String::encodeBase32Default,
            decode = String::decodeBase32
        ),
        Base64(
            ok = String::isBase64,
            encode = String::encodeBase64,
            decode = String::decodeBase64
        )
    }

    enum class Error {
        Encode,
        Decode
    }
}