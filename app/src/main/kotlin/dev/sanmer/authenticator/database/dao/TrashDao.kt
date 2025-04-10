package dev.sanmer.authenticator.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import dev.sanmer.authenticator.database.entity.TrashEntity
import dev.sanmer.authenticator.database.entity.TrashWithSecret
import kotlinx.coroutines.flow.Flow

@Dao
interface TrashDao {
    @Transaction
    @Query("SELECT * FROM trash")
    fun getAllWithSecretAsFlow(): Flow<List<TrashWithSecret>>

    @Query("SELECT * FROM trash")
    suspend fun getAll(): List<TrashEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(value: TrashEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(values: List<TrashEntity>)

    @Query("DELETE FROM trash WHERE secret = :secret")
    suspend fun delete(secret: String)

    @Query("DELETE FROM trash WHERE secret IN (:secrets)")
    suspend fun delete(secrets: List<String>)

    @Query("DELETE FROM trash")
    suspend fun deleteAll()
}