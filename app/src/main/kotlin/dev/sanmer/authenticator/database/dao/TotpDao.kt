package dev.sanmer.authenticator.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import dev.sanmer.authenticator.database.entity.TotpEntity
import dev.sanmer.authenticator.database.entity.TotpWithTrash
import kotlinx.coroutines.flow.Flow

@Dao
interface TotpDao {
    @Query("SELECT * FROM totp")
    fun getAllAsFlow(): Flow<List<TotpEntity>>

    @Transaction
    @Query("SELECT * FROM totp")
    fun getAllWithTrashAsFlow(): Flow<List<TotpWithTrash>>

    @Query("SELECT * FROM totp WHERE secret = :secret")
    fun getBySecretAsFlow(secret: String): Flow<TotpEntity>

    @Query("SELECT * FROM totp")
    suspend fun getAll(): List<TotpEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entities: List<TotpEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(entity: TotpEntity)

    @Query("DELETE FROM totp WHERE secret = :secret")
    suspend fun delete(secret: String)

    @Query("DELETE FROM totp WHERE secret IN (:secret)")
    suspend fun delete(secret: List<String>)

    @Query("DELETE FROM totp")
    suspend fun deleteAll()

    @Query("SELECT EXISTS(SELECT 1 FROM totp WHERE secret = :secret)")
    suspend fun exists(secret: String): Boolean
}