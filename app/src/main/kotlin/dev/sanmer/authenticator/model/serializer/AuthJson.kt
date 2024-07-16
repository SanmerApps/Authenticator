package dev.sanmer.authenticator.model.serializer

import dev.sanmer.authenticator.model.auth.Auth
import dev.sanmer.authenticator.model.auth.HotpAuth
import dev.sanmer.authenticator.model.auth.TotpAuth
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.InputStream
import java.io.OutputStream

@Serializable
data class AuthJson(
    val hotp: List<HotpSerializable> = emptyList(),
    val totp: List<TotpSerializable> = emptyList()
) {
    constructor(
        auths: List<Auth>
    ) : this(
        hotp = auths.filterIsInstance<HotpAuth>().map(::HotpSerializable),
        totp = auths.filterIsInstance<TotpAuth>().map(::TotpSerializable)
    )

    val auths: List<OtpSerializable>
        get() = hotp.toMutableList<OtpSerializable>()
            .apply { addAll(totp) }
            .toList()

    fun encodeTo(output: OutputStream) {
        endpointJson.encodeToStream(this, output)
    }

    companion object {
        private val endpointJson = Json {
            prettyPrint = true
        }

        const val MIME_TYPE = "application/json"
        const val FILE_NAME = "auth.json"

        fun decodeFrom(input: InputStream): AuthJson =
            Json.decodeFromStream(input)
    }
}
