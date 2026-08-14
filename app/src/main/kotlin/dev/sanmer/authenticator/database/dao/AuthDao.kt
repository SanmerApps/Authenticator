package dev.sanmer.authenticator.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
import dev.sanmer.authenticator.database.model.Auth
import dev.sanmer.authenticator.database.model.AuthProperties
import dev.sanmer.authenticator.database.model.AuthProperty
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

@Dao
interface AuthDao {
    @Transaction
    @Query("SELECT * FROM Auth")
    suspend fun getAllAuthProperties(): List<AuthProperties>

    @Transaction
    @Query("SELECT * FROM Auth WHERE trashedAt = 0")
    suspend fun getUntrashedAuthProperties(): List<AuthProperties>

    @Query("SELECT * FROM AuthProperty WHERE key IN (:key)")
    suspend fun getProperties(vararg key: AuthProperty.Key): List<AuthProperty>

    @Transaction
    @Query("SELECT * FROM Auth")
    fun getAllAuthPropertiesAsFlow(): Flow<List<AuthProperties>>

    @Transaction
    @Query("SELECT * FROM Auth WHERE trashedAt = 0")
    fun getUntrashedAuthPropertiesAsFlow(): Flow<List<AuthProperties>>

    @Transaction
    @Query("SELECT * FROM Auth WHERE trashedAt != 0")
    fun getTrashedAuthAsFlow(): Flow<List<Auth>>

    @Query("SELECT COUNT(*) FROM Auth WHERE trashedAt != 0")
    fun getTrashedCountAsFlow(): Flow<Int>

    @Transaction
    @Query("SELECT * FROM Auth WHERE id = :id")
    fun getAuthPropertiesAsFlow(id: Long): Flow<AuthProperties?>

    @Upsert
    suspend fun upsertAuth(auth: Auth): Long

    @Query("UPDATE Auth SET trashedAt = :trashedAt WHERE id = :id")
    suspend fun trashAuth(id: Long, trashedAt: Instant)

    @Query("DELETE FROM Auth WHERE id = :id")
    suspend fun deleteAuth(id: Long)

    @Upsert
    suspend fun upsertProperties(properties: List<AuthProperty>)

    @Query("DELETE FROM AuthProperty WHERE authId = :authId")
    suspend fun deleteProperties(authId: Long)

    @Transaction
    suspend fun upsert(auth: Auth, properties: List<AuthProperty>) {
        val authId = upsertAuth(auth).coerceAtLeast(auth.id)
        upsertProperties(properties.map { it.copy(authId = authId) })
    }

    @Transaction
    suspend fun delete(authId: Long) {
        deleteAuth(authId)
        deleteProperties(authId)
    }
}