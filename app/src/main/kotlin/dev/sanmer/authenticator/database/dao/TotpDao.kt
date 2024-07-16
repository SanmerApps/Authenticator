package dev.sanmer.authenticator.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.sanmer.authenticator.database.entity.TotpEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TotpDao {
    @Query("SELECT * FROM totp")
    fun getAllAsFlow(): Flow<List<TotpEntity>>

    @Query("SELECT * FROM totp WHERE secret = :secret")
    fun getBySecretAsFlow(secret: String): Flow<TotpEntity?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(value: TotpEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(values: List<TotpEntity>)

    @Update
    suspend fun update(value: TotpEntity)

    @Delete
    suspend fun delete(value: TotpEntity)

    @Delete
    suspend fun delete(values: List<TotpEntity>)

    @Query("SELECT EXISTS(SELECT 1 FROM totp WHERE secret = :secret)")
    suspend fun exists(secret: String): Boolean
}