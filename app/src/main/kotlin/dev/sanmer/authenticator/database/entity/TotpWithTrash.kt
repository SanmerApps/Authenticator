package dev.sanmer.authenticator.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class TotpWithTrash(
    @Embedded
    val totp: TotpEntity,
    @Relation(parentColumn = "secret", entityColumn = "secret")
    val trash: TrashEntity?
)