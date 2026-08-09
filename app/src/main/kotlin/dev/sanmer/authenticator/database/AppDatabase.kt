package dev.sanmer.authenticator.database

import android.content.Context
import androidx.room3.ColumnTypeConverter
import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.room3.migration.Migration
import androidx.sqlite.execSQL
import dev.sanmer.authenticator.database.dao.AuthDao
import dev.sanmer.authenticator.database.model.Auth
import dev.sanmer.authenticator.database.model.AuthProperty
import kotlin.time.Instant

@Database(
    entities = [
        Auth::class,
        AuthProperty::class
    ],
    version = 3
)
@ColumnTypeConverters(AppDatabase.Default::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun auth(): AuthDao

    companion object Default {
        @ColumnTypeConverter
        fun fromEpochMilliseconds(value: Long) = Instant.fromEpochMilliseconds(value)

        @ColumnTypeConverter
        fun toEpochMilliseconds(value: Instant) = value.toEpochMilliseconds()

        fun build(context: Context) =
            Room.databaseBuilder<AppDatabase>(
                context = context,
                name = "auth"
            ).addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3
            ).build()

        private val MIGRATION_1_2 = Migration(1, 2) { connection ->
            connection.execSQL("DROP TABLE trash")
            connection.execSQL("DROP TABLE hotp")

            connection.execSQL("CREATE TABLE totp_new (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, deletedAt INTEGER NOT NULL, issuer TEXT NOT NULL, name TEXT NOT NULL, secret TEXT NOT NULL, hash TEXT NOT NULL, digits INTEGER NOT NULL, period INTEGER NOT NULL)")
            connection.execSQL("INSERT INTO totp_new (deletedAt, issuer, name, secret, hash, digits, period) SELECT 0, issuer, name, secret, hash, digits, period FROM totp")
            connection.execSQL("DROP TABLE totp")
            connection.execSQL("ALTER TABLE totp_new RENAME TO totp")
        }

        private val MIGRATION_2_3 = Migration(2, 3) { connection ->
            connection.execSQL("CREATE TABLE Auth (id INTEGER PRIMARY KEY NOT NULL, name TEXT NOT NULL, issuer TEXT NOT NULL, type TEXT NOT NULL, trashedAt INTEGER NOT NULL)")
            connection.execSQL("INSERT INTO Auth (id, name, issuer, type, trashedAt) SELECT id, name, issuer, 'TOTP', deletedAt FROM totp")

            connection.execSQL("CREATE TABLE AuthProperty (authId INTEGER NOT NULL, key TEXT NOT NULL, value TEXT NOT NULL, PRIMARY KEY(authId, key))")
            connection.execSQL("INSERT INTO AuthProperty (authId, key, value) SELECT id, 'Secret', secret FROM totp")
            connection.execSQL("INSERT INTO AuthProperty (authId, key, value) SELECT id, 'Hash', hash FROM totp")
            connection.execSQL("INSERT INTO AuthProperty (authId, key, value) SELECT id, 'Digits', CAST(digits AS TEXT) FROM totp")
            connection.execSQL("INSERT INTO AuthProperty (authId, key, value) SELECT id, 'Period', CAST(period AS TEXT) FROM totp")
            connection.execSQL("DROP TABLE totp")
        }
    }
}