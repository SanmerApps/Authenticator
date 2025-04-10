package dev.sanmer.authenticator.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class TrashWithSecret(
    @Embedded
    val trash: TrashEntity,
    @Relation(parentColumn = "secret", entityColumn = "secret")
    val hotp: HotpEntity?,
    @Relation(parentColumn = "secret", entityColumn = "secret")
    val totp: TotpEntity?
)