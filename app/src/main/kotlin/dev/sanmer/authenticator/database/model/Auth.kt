package dev.sanmer.authenticator.database.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import dev.sanmer.authenticator.Const.INSTANT_ZERO
import kotlin.time.Instant

@Entity
data class Auth(
    @PrimaryKey(
        autoGenerate = true,
        algorithm = PrimaryKey.Algorithm.ROWID
    )
    val id: Long = 0,
    val name: String,
    val issuer: String,
    val type: Type,
    val trashedAt: Instant = INSTANT_ZERO
) {
    enum class Type {
        TOTP
    }
}
