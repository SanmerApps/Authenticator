package dev.sanmer.authenticator.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.sanmer.authenticator.database.entity.HotpEntity
import dev.sanmer.authenticator.database.entity.TrashEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HotpDao {
    @Query("SELECT * FROM hotp")
    fun getAll(): List<HotpEntity>

    @Query("SELECT * FROM hotp")
    fun getAllAsFlow(): Flow<List<HotpEntity>>

    @Query("SELECT * FROM hotp WHERE secret = :secret")
    fun getBySecretAsFlow(secret: String): Flow<HotpEntity?>

    @Query("SELECT * FROM hotp LEFT JOIN trash ON trash.secret = hotp.secret")
    fun getMapToTrashAsFlow(): Flow<Map<HotpEntity, TrashEntity?>>

    @Query("SELECT * FROM hotp JOIN trash ON trash.secret = hotp.secret")
    fun getAllWithTrashAsFlow(): Flow<Map<HotpEntity, TrashEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entities: List<HotpEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(entity: HotpEntity)

    @Query("DELETE FROM hotp WHERE secret = :secret")
    suspend fun delete(secret: String)

    @Query("DELETE FROM hotp WHERE secret IN (:secret)")
    suspend fun delete(secret: List<String>)

    @Query("DELETE FROM hotp")
    suspend fun deleteAll()

    @Query("SELECT EXISTS(SELECT 1 FROM hotp WHERE secret = :secret)")
    suspend fun exists(secret: String): Boolean
}