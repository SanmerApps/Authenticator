package dev.sanmer.authenticator.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.sanmer.authenticator.database.entity.TrashEntity

@Dao
interface TrashDao {
    @Query("SELECT * FROM trash WHERE timestamp < :before")
    suspend fun getByTimestamp(before: Long): List<TrashEntity>

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