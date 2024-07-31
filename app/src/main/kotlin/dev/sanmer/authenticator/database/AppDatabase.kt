package dev.sanmer.authenticator.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.sanmer.authenticator.database.dao.HotpDao
import dev.sanmer.authenticator.database.dao.TotpDao
import dev.sanmer.authenticator.database.dao.TrashDao
import dev.sanmer.authenticator.database.entity.HotpEntity
import dev.sanmer.authenticator.database.entity.TotpEntity
import dev.sanmer.authenticator.database.entity.TrashEntity
import dev.sanmer.authenticator.ktx.deviceProtectedContext
import dev.sanmer.otp.HOTP
import javax.inject.Singleton

@Database(version = 1, entities = [TrashEntity::class, HotpEntity::class, TotpEntity::class])
@TypeConverters(AppDatabase.Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trash(): TrashDao
    abstract fun hotp(): HotpDao
    abstract fun totp(): TotpDao

    companion object Default {
        fun build(context: Context) =
            Room.databaseBuilder(
                context, AppDatabase::class.java, "auth"
            ).build()
    }

    @Suppress("FunctionName")
    object Converters {
        @TypeConverter
        fun StringToHash(value: String) = HOTP.Hash.valueOf(value)

        @TypeConverter
        fun HashToString(value: HOTP.Hash) = value.name
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object Provider {
        @Provides
        @Singleton
        fun AppDatabase(
            @ApplicationContext context: Context
        ) = build(context.deviceProtectedContext)

        @Provides
        @Singleton
        fun TrashDao(db: AppDatabase) = db.trash()

        @Provides
        @Singleton
        fun HotpDao(db: AppDatabase) = db.hotp()

        @Provides
        @Singleton
        fun TotpDao(db: AppDatabase) = db.totp()
    }
}