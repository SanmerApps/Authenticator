package dev.sanmer.authenticator.database.model

import androidx.room3.Entity

@Entity(primaryKeys = ["authId", "key"])
data class AuthProperty(
    val authId: Long = 0,
    val key: Key,
    val value: String
) {
    enum class Key {
        Secret,
        Hash,
        Digits,
        Period
    }
}
