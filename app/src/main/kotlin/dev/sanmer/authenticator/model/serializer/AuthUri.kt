package dev.sanmer.authenticator.model.serializer

import dev.sanmer.authenticator.database.entity.TotpEntity
import dev.sanmer.otp.Otp
import dev.sanmer.otp.OtpUri
import dev.sanmer.otp.OtpUri.Default.toOtpUri
import java.io.InputStream
import java.io.OutputStream

data class AuthUri(
    val totp: List<TotpEntity>
) {
    fun encodeTo(output: OutputStream) {
        val uris = totp.map { it.uri().toString() }
        val content = uris.joinToString(separator = "\n")
        output.write(content.toByteArray())
    }

    companion object Default {
        const val MIME_TYPE = "text/plain"
        const val FILE_NAME = "auth.txt"

        fun TotpEntity.uri() = OtpUri(
            type = "totp",
            name = name,
            issuer = issuer,
            secret = secret,
            algorithm = hash.name,
            digits = digits,
            period = period
        )

        fun OtpUri.toTotpEntity(): TotpEntity {
            require(type.equals("totp", ignoreCase = true)) { "Expected type = totp" }
            val hash = algorithm?.let { Otp.Hash.valueOf(it.uppercase()) }
            return TotpEntity(
                issuer = issuer,
                name = name,
                secret = secret,
                hash = hash ?: Otp.Hash.SHA1,
                digits = digits ?: 6,
                period = period ?: 30,
            )
        }

        fun decodeFrom(input: InputStream): AuthUri {
            val totp = input.bufferedReader()
                .lineSequence()
                .mapNotNull {
                    runCatching {
                        it.toOtpUri().toTotpEntity()
                    }.getOrNull()
                }
            return AuthUri(
                totp = totp.toList()
            )
        }
    }
}