package dev.sanmer.authenticator.ui.screens.encode

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import dev.sanmer.authenticator.Logger
import dev.sanmer.encoding.decodeBase32
import dev.sanmer.encoding.decodeBase64
import dev.sanmer.encoding.encodeBase32Default
import dev.sanmer.encoding.encodeBase64

class EncodeViewModel : ViewModel() {
    var encoded by mutableStateOf(TextFieldValue())
        private set

    var decoded by mutableStateOf(TextFieldValue())
        private set

    var type by mutableStateOf(Type.Base64)
        private set

    private val errors = mutableStateMapOf<Error, Boolean>()

    private val logger = Logger.Android("EncodeViewModel")

    init {
        logger.d("init")
    }

    fun updateEncoded(value: TextFieldValue) {
        encoded = value
    }

    fun updateDecoded(value: TextFieldValue) {
        decoded = value
    }

    fun updateType(value: Type) {
        type = value
    }

    fun isError(value: Error) =
        errors.getOrDefault(value, false)

    fun encode() {
        errors[Error.Decode] = false
        errors[Error.Encode] = runCatching {
            encoded = type.encode(decoded.text).asTextFieldValue()
        }.isFailure
    }

    fun decode() {
        errors[Error.Encode] = false
        errors[Error.Decode] = runCatching {
            decoded = type.decode(encoded.text).asTextFieldValue()
        }.isFailure
    }

    private fun String.asTextFieldValue() =
        TextFieldValue(
            text = this,
            selection = TextRange(length)
        )

    enum class Type(
        val encode: (String) -> String,
        val decode: (String) -> String
    ) {
        Base32(
            encode = String::encodeBase32Default,
            decode = String::decodeBase32
        ),
        Base64(
            encode = String::encodeBase64,
            decode = String::decodeBase64
        )
    }

    enum class Error {
        Encode,
        Decode
    }
}