package dev.sanmer.authenticator.model.serializer

import dev.sanmer.authenticator.model.auth.Auth
import dev.sanmer.otp.HOTP
import dev.sanmer.otp.OtpUri

interface OtpSerializable {
    val auth: Auth

    companion object {
        fun String.decodeOtpAuth(): OtpSerializable {
            val uri = OtpUri.parse(this)
            val type = uri.type.uppercase().let(Auth.Type::valueOf)
            val hash = uri.algorithm.uppercase().let(HOTP.Hash::valueOf)

            return when (type) {
                Auth.Type.HOTP -> HotpSerializable(
                    issuer = uri.issuer,
                    name = uri.name,
                    secret = uri.secret,
                    hash = hash,
                    digits = uri.digits,
                    counter = uri.counter ?: 0L,
                )

                Auth.Type.TOTP -> TotpSerializable(
                    issuer = uri.issuer,
                    name = uri.name,
                    secret = uri.secret,
                    hash = hash,
                    digits = uri.digits,
                    period = uri.period ?: 30L,
                )
            }
        }
    }
}