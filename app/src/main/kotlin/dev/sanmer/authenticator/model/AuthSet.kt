package dev.sanmer.authenticator.model

import android.net.Uri
import dev.sanmer.authenticator.database.model.Auth
import dev.sanmer.authenticator.database.model.AuthProperties
import dev.sanmer.authenticator.model.OtpUri.Default.toOtpUri
import dev.sanmer.authenticator.model.otp.Totp
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.InputStream
import java.io.OutputStream

@Serializable
data class AuthSet(
    val totp: List<Totp>
) {
    inline fun <T> map(action: (AuthProperties) -> T): List<T> {
        val list = mutableListOf<T>()
        totp.mapTo(list) { action(it.toAuth()) }
        return list
    }

    fun encodeToJson(output: OutputStream) {
        endpointJson.encodeToStream(this, output)
    }

    fun encodeToUri(output: OutputStream) {
        val writer = output.bufferedWriter()
        totp.forEach { writer.appendLine(it.toUri().toString()) }
        writer.flush()
    }

    companion object Default {
        private val endpointJson = Json {
            prettyPrint = true
        }

        fun List<AuthProperties>.toAuthSet(): AuthSet {
            val totp = mutableListOf<Totp>()
            forEach {
                when (it.auth.type) {
                    Auth.Type.TOTP -> totp.add(Totp(it))
                }
            }
            return AuthSet(
                totp = totp
            )
        }

        fun decodeFromJson(input: InputStream) = endpointJson.decodeFromStream<AuthSet>(input)

        fun decodeFromUri(input: InputStream): AuthSet {
            val totp = mutableListOf<Totp>()
            input.bufferedReader().forEachLine {
                runCatching {
                    val otpUri = Uri.parse(it).toOtpUri()
                    when (otpUri.type) {
                        OtpUri.Type.TOTP -> totp.add(Totp(otpUri))
                    }
                }
            }
            return AuthSet(
                totp = totp
            )
        }
    }
}
