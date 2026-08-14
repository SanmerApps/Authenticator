package dev.sanmer.authenticator.model.otp

import dev.sanmer.auth.Otp
import dev.sanmer.auth.OtpUri
import dev.sanmer.authenticator.database.model.Auth
import dev.sanmer.authenticator.database.model.AuthProperties
import dev.sanmer.authenticator.database.model.AuthProperty
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

    constructor(uri: OtpUri) : this(
        name = uri.name,
        issuer = uri.issuer,
        secret = uri.secret,
        hash = uri.algorithm?.let(Otp.Hash::valueOf) ?: Otp.Hash.SHA1,
        digits = uri.digits ?: 6,
        period = uri.period ?: 30
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
        type = "totp",
        name = name,
        issuer = issuer,
        secret = secret,
        algorithm = hash.name,
        digits = digits,
        period = period
    ).toUri()
}