package dev.sanmer.authenticator.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.sanmer.authenticator.database.entity.TotpEntity
import dev.sanmer.authenticator.database.entity.TrashEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TotpDao {
    @Query("SELECT * FROM totp")
    fun getAllAsFlow(): Flow<List<TotpEntity>>

    @Query("SELECT * FROM totp WHERE secret = :secret")
    fun getBySecretAsFlow(secret: String): Flow<TotpEntity?>

    @Query("SELECT * FROM totp LEFT JOIN trash ON trash.secret = totp.secret")
    fun getMapToTrashAsFlow(): Flow<Map<TotpEntity, TrashEntity?>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entities: List<TotpEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(entity: TotpEntity)

    @Query("DELETE FROM totp WHERE secret = :secret")
    suspend fun delete(secret: String)

    @Query("DELETE FROM totp WHERE secret IN (:secret)")
    suspend fun delete(secret: List<String>)

    @Query("SELECT EXISTS(SELECT 1 FROM totp WHERE secret = :secret)")
    suspend fun exists(secret: String): Boolean
}