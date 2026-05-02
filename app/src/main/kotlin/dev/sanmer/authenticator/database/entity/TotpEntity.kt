package dev.sanmer.authenticator.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.sanmer.otp.Otp
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
    val hash: Otp.Hash,
    val digits: Int,
    val period: Long
) {
    val lifetime by lazy { (System.currentTimeMillis() - deletedAt).milliseconds }
    val displayName by lazy { "$issuer ($name)" }

    fun toTrash() = copy(deletedAt = System.currentTimeMillis())

    companion object Default {
        val LIFETIME_MAX = 7.days
    }
}
