package dev.sanmer.authenticator.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.sanmer.authenticator.database.dao.TotpDao
import dev.sanmer.authenticator.database.entity.TotpEntity
import dev.sanmer.authenticator.ktx.deviceProtectedContext
import dev.sanmer.otp.HOTP
import javax.inject.Singleton

@Database(version = 2, entities = [TotpEntity::class])
@TypeConverters(AppDatabase.Converter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun totp(): TotpDao

    companion object Build {
        operator fun invoke(context: Context) =
            Room.databaseBuilder(
                context, AppDatabase::class.java, "auth"
            ).addMigrations(
                MIGRATION_1_2
            ).build()

        private val MIGRATION_1_2 = Migration(1, 2) {
            it.execSQL("DROP TABLE trash")
            it.execSQL("DROP TABLE hotp")

            it.execSQL("CREATE TABLE IF NOT EXISTS totp_2 (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, deletedAt INTEGER NOT NULL, issuer TEXT NOT NULL, name TEXT NOT NULL, secret TEXT NOT NULL, hash TEXT NOT NULL, digits INTEGER NOT NULL, period INTEGER NOT NULL)")
            it.execSQL("INSERT INTO totp_2 (deletedAt, issuer, name, secret, hash, digits, period) SELECT 0, issuer, name, secret, hash, digits, period FROM totp")
            it.execSQL("DROP TABLE totp")
            it.execSQL("ALTER TABLE totp_2 RENAME TO totp")
        }
    }

    @Suppress("FunctionName")
    object Converter {
        @TypeConverter
        fun StringToHash(value: String) = HOTP.Hash.valueOf(value)

        @TypeConverter
        fun HashToString(value: HOTP.Hash) = value.name
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object Impl {
        @Provides
        @Singleton
        fun AppDatabase(
            @ApplicationContext context: Context
        ) = Build(context.deviceProtectedContext)

        @Provides
        @Singleton
        fun TotpDao(db: AppDatabase) = db.totp()
    }
}