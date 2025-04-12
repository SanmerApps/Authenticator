package dev.sanmer.authenticator.model.serializer

import dev.sanmer.authenticator.model.AuthType
import dev.sanmer.otp.HOTP
import dev.sanmer.otp.OtpUri
import kotlinx.serialization.Serializable

@Serializable
data class TotpAuth(
    val issuer: String,
    val name: String,
    val secret: String,
    val hash: HOTP.Hash,
    val digits: Int,
    val period: Long
) {
    val uri get() = OtpUri(
        type = AuthType.TOTP.name,
        name = name,
        issuer = issuer,
        secret = secret,
        algorithm = hash.name,
        digits = digits,
        period = period
    )
}
