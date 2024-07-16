package dev.sanmer.authenticator.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.sanmer.authenticator.database.entity.HotpEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HotpDao {
    @Query("SELECT * FROM hotp")
    fun getAllAsFlow(): Flow<List<HotpEntity>>

    @Query("SELECT * FROM hotp WHERE secret = :secret")
    fun getBySecretAsFlow(secret: String): Flow<HotpEntity?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(value: HotpEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(values: List<HotpEntity>)

    @Update
    suspend fun update(value: HotpEntity)

    @Delete
    suspend fun delete(value: HotpEntity)

    @Delete
    suspend fun delete(values: List<HotpEntity>)

    @Query("SELECT EXISTS(SELECT 1 FROM hotp WHERE secret = :secret)")
    suspend fun exists(secret: String): Boolean
}