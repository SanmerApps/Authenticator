package dev.sanmer.authenticator.model.serializer

import dev.sanmer.authenticator.model.auth.Auth
import dev.sanmer.authenticator.model.auth.TotpAuth
import dev.sanmer.otp.HOTP
import dev.sanmer.otp.OtpUri
import kotlinx.serialization.Serializable

@Serializable
data class TotpSerializable(
    val issuer: String,
    val name: String,
    val secret: String,
    val hash: HOTP.Hash,
    val digits: Int,
    val period: Long
) : OtpSerializable {
    constructor(
        original: TotpAuth
    ) : this(
        issuer = original.issuer,
        name = original.name,
        secret = original.secret,
        hash = original.hash,
        digits = original.digits,
        period = original.period
    )

    override val auth get() = TotpAuth(
        issuer = issuer,
        name = name,
        secret = secret,
        hash = hash,
        digits = digits,
        period = period
    )

    override val uri get() = OtpUri(
        type = Auth.Type.TOTP.name,
        name = name,
        issuer = issuer,
        secret = secret,
        algorithm = hash.name,
        digits = digits,
        period = period
    )
}
