package dev.sanmer.authenticator.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.sanmer.authenticator.database.dao.HotpDao
import dev.sanmer.authenticator.database.dao.TotpDao
import dev.sanmer.authenticator.database.entity.HotpEntity
import dev.sanmer.authenticator.database.entity.TotpEntity

@Database(
    entities = [
        HotpEntity::class,
        TotpEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hotp(): HotpDao
    abstract fun totp(): TotpDao

    companion object {
        fun build(context: Context) =
            Room.databaseBuilder(
                context,
                AppDatabase::class.java, "auth"
            ).build()
    }
}