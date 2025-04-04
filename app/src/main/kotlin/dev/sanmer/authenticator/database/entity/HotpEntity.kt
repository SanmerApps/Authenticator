package dev.sanmer.authenticator.database.entity

import androidx.room.Entity
import dev.sanmer.authenticator.model.auth.HotpAuth
import dev.sanmer.otp.HOTP

@Entity(tableName = "hotp", primaryKeys = ["secret"])
data class HotpEntity(
    val issuer: String,
    val name: String,
    val secret: String,
    val hash: HOTP.Hash,
    val digits: Int,
    val counter: Long
) {
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

    fun auth() = HotpAuth(
        issuer = issuer,
        name = name,
        secret = secret,
        hash = hash,
        digits = digits,
        counter = counter
    )
}
