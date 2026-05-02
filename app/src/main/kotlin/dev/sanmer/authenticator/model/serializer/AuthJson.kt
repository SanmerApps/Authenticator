package dev.sanmer.authenticator.model.serializer

import dev.sanmer.authenticator.database.entity.TotpEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.InputStream
import java.io.OutputStream

@Serializable
data class AuthJson(
    val totp: List<TotpJson>
) {
    fun entities() = totp.map(TotpJson::entity)

    fun encodeTo(output: OutputStream) {
        endpointJson.encodeToStream(this, output)
    }

    companion object Default {
        private val endpointJson = Json {
            prettyPrint = true
        }

        const val MIME_TYPE = "application/json"
        const val FILE_NAME = "auth.json"

        fun entities(
            totp: List<TotpEntity>
        ) = AuthJson(
            totp = totp.map(::TotpJson)
        )

        fun decodeFrom(input: InputStream): AuthJson =
            Json.decodeFromStream(input)
    }
}
