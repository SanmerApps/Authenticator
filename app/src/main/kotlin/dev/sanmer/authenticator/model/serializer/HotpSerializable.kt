package dev.sanmer.authenticator.model.serializer

import dev.sanmer.authenticator.model.auth.Auth
import dev.sanmer.authenticator.model.auth.HotpAuth
import dev.sanmer.otp.HOTP
import dev.sanmer.otp.OtpUri
import kotlinx.serialization.Serializable

@Serializable
data class HotpSerializable(
    val issuer: String,
    val name: String,
    val secret: String,
    val hash: HOTP.Hash,
    val digits: Int,
    val counter: Long
) : OtpSerializable {
    constructor(
        original: HotpAuth
    ) : this(
        issuer = original.issuer,
        name = original.name,
        secret = original.secret,
        hash = original.hash,
        digits = original.digits,
        counter = original.counter
    )

    override val auth get() = HotpAuth(
        issuer = issuer,
        name = name,
        secret = secret,
        hash = hash,
        digits = digits,
        count = counter
    )

    override val uri get() = OtpUri(
        type = Auth.Type.HOTP.name,
        name = name,
        issuer = issuer,
        secret = secret,
        algorithm = hash.name,
        digits = digits,
        counter = counter
    )
}
