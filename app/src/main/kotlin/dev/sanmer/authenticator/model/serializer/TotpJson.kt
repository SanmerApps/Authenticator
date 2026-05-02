package dev.sanmer.authenticator.model.serializer

import dev.sanmer.authenticator.database.entity.TotpEntity
import dev.sanmer.otp.Otp
import kotlinx.serialization.Serializable

@Serializable
data class TotpJson(
    val issuer: String,
    val name: String,
    val secret: String,
    val hash: Otp.Hash,
    val digits: Int,
    val period: Long
) {
    constructor(entity: TotpEntity) : this(
        issuer = entity.issuer,
        name = entity.name,
        secret = entity.secret,
        hash = entity.hash,
        digits = entity.digits,
        period = entity.period
    )

    fun entity() = TotpEntity(
        issuer = issuer,
        name = name,
        secret = secret,
        hash = hash,
        digits = digits,
        period = period
    )
}
