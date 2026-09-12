package dev.sanmer.authenticator.model.otp

import dev.sanmer.auth.Otp
import dev.sanmer.authenticator.database.model.Auth
import dev.sanmer.authenticator.database.model.AuthProperties
import dev.sanmer.authenticator.database.model.AuthProperty
import dev.sanmer.authenticator.model.OtpUri
import kotlinx.serialization.Serializable

@Serializable
data class Totp(
    val name: String,
    val issuer: String,
    val secret: String,
    val hash: Otp.Hash,
    val digits: Int,
    val period: Long
) {
    constructor(auth: AuthProperties) : this(
        name = auth.auth.name,
        issuer = auth.auth.issuer,
        secret = auth.getValue(AuthProperty.Key.Secret) { it },
        hash = auth.getValue(AuthProperty.Key.Hash, Otp.Hash::valueOf),
        digits = auth.getValue(AuthProperty.Key.Digits, String::toInt),
        period = auth.getValue(AuthProperty.Key.Period, String::toLong)
    )

    constructor(otpUri: OtpUri) : this(
        name = otpUri.name,
        issuer = otpUri.issuer,
        secret = otpUri.secret,
        hash = otpUri.hash,
        digits = otpUri.digits,
        period = otpUri.period
    )

    fun toAuth(id: Long = 0) = AuthProperties.build(
        auth = Auth(
            id = id,
            name = name,
            issuer = issuer,
            type = Auth.Type.TOTP
        ),
        properties = listOf(
            AuthProperty.Key.Secret to secret,
            AuthProperty.Key.Hash to hash.name,
            AuthProperty.Key.Digits to digits.toString(),
            AuthProperty.Key.Period to period.toString()
        )
    )

    fun toUri() = OtpUri(
        type = OtpUri.Type.TOTP,
        name = name,
        issuer = issuer,
        secret = secret,
        hash = hash,
        digits = digits,
        period = period
    ).toUri()
}