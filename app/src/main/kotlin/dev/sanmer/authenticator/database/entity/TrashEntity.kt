package dev.sanmer.authenticator.database.entity

import androidx.room.Entity

@Entity(
    tableName = "trash",
    primaryKeys = ["secret"]
)
data class TrashEntity(
    val secret: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        const val LIFETIME = 604_800_000L
        val lifetime: Long inline get() = System.currentTimeMillis() - LIFETIME
    }
}