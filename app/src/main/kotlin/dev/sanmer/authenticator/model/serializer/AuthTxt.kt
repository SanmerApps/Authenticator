package dev.sanmer.authenticator.model.serializer

import dev.sanmer.authenticator.model.AuthType
import dev.sanmer.otp.HOTP
import dev.sanmer.otp.OtpUri.Default.toOtpUri
import java.io.InputStream
import java.io.OutputStream
import kotlin.streams.toList

data class AuthTxt(
    val totp: List<TotpAuth>
) {
    fun encodeTo(output: OutputStream) {
        val uris = totp.map { it.uri.toString() }
        val content = uris.joinToString(separator = "\n")
        output.write(content.toByteArray())
    }

    companion object Default {
        const val MIME_TYPE = "text/plain"
        const val FILE_NAME = "auth.txt"

        fun parse(uriString: String): TotpAuth {
            val uri = uriString.toOtpUri()
            AuthType.valueOf(uri.type)

            return TotpAuth(
                issuer = uri.issuer,
                name = uri.name,
                secret = uri.secret,
                hash = uri.algorithm?.let(HOTP.Hash::valueOf) ?: HOTP.Hash.SHA1,
                digits = uri.digits ?: 6,
                period = uri.period ?: 30,
            )
        }

        fun decodeFrom(input: InputStream): AuthTxt {
            return AuthTxt(
                input.bufferedReader().lines().toList<String>()
                    .mapNotNull {
                        runCatching {
                            parse(it)
                        }.getOrNull()
                    }
            )
        }
    }
}