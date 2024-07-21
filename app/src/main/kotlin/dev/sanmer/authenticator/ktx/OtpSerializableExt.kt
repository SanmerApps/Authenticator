package dev.sanmer.authenticator.ktx

import dev.sanmer.authenticator.model.auth.Auth
import dev.sanmer.authenticator.model.serializer.HotpSerializable
import dev.sanmer.authenticator.model.serializer.OtpSerializable
import dev.sanmer.authenticator.model.serializer.TotpSerializable
import dev.sanmer.otp.HOTP
import dev.sanmer.otp.OtpUri.Companion.toOtpUri

fun String.decodeOtpAuth(): OtpSerializable {
    val uri = toOtpUri()
    val type = uri.type.let(Auth.Type::valueOf)
    val hash = uri.algorithm.let(HOTP.Hash::valueOf)

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