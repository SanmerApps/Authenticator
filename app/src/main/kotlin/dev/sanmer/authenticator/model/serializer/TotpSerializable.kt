package dev.sanmer.authenticator.model.serializer

import dev.sanmer.authenticator.model.auth.TotpAuth
import dev.sanmer.otp.HOTP
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
}
