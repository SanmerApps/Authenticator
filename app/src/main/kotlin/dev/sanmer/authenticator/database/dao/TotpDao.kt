package dev.sanmer.authenticator.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.sanmer.authenticator.database.entity.TotpEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TotpDao {
    @Query("SELECT * FROM totp WHERE deletedAt != 0")
    suspend fun getAllTrashed(): List<TotpEntity>

    @Query("SELECT * FROM totp WHERE deletedAt = 0")
    fun getAllEnabledAsFlow(): Flow<List<TotpEntity>>

    @Query("SELECT * FROM totp WHERE deletedAt != 0")
    fun getAllTrashedAsFlow(): Flow<List<TotpEntity>>

    @Query("SELECT * FROM totp WHERE id = :id")
    fun getByIdAsFlow(id: Long): Flow<TotpEntity?>

    @Query("SELECT * FROM totp")
    suspend fun getAll(): List<TotpEntity>

    @Insert
    suspend fun insert(entity: TotpEntity)

    @Insert
    suspend fun insert(entities: List<TotpEntity>)

    @Update
    suspend fun update(entity: TotpEntity)

    @Update
    suspend fun update(entities: List<TotpEntity>)

    @Delete
    suspend fun delete(entity: TotpEntity)

    @Delete
    suspend fun delete(entities: List<TotpEntity>)
}