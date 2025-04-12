package dev.sanmer.authenticator.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.sanmer.authenticator.model.serializer.TotpAuth
import dev.sanmer.otp.HOTP
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

@Entity(tableName = "totp")
data class TotpEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val deletedAt: Long = 0,
    val issuer: String,
    val name: String,
    val secret: String,
    val hash: HOTP.Hash,
    val digits: Int,
    val period: Long
) {
    val lifetime inline get() = (System.currentTimeMillis() - deletedAt).milliseconds

    val displayName inline get() = "$issuer ($name)"

    constructor(
        auth: TotpAuth
    ) : this(
        issuer = auth.issuer,
        name = auth.name,
        secret = auth.secret,
        hash = auth.hash,
        digits = auth.digits,
        period = auth.period
    )

    val totp get() = TotpAuth(
        issuer = issuer,
        name = name,
        secret = secret,
        hash = hash,
        digits = digits,
        period = period
    )

    companion object Default {
        val LIFETIME_MAX = 7.days
    }
}
