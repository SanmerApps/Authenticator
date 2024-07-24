package dev.sanmer.authenticator.database.entity

import androidx.room.Entity
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

@Entity(
    tableName = "trash",
    primaryKeys = ["secret"]
)
data class TrashEntity(
    val secret: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    val lifetime by lazy {
        (System.currentTimeMillis() - timestamp).milliseconds
    }

    companion object {
        val LIFETIME_MAX = 7.days
    }
}