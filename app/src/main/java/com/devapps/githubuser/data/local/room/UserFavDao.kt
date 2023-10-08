package com.devapps.githubuser.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.*
import com.devapps.githubuser.data.local.entity.UserFavEntity

@Dao
interface UserFavDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(userFavEntity: UserFavEntity)

    @Delete
    suspend fun delete(userFavEntity: UserFavEntity)

    @Query("SELECT * FROM userfaventity ORDER BY username ASC")
    fun getAllUser(): LiveData<List<UserFavEntity>>

    @Query("SELECT EXISTS (SELECT 1 FROM userfaventity WHERE username = :username LIMIT 1)")
    suspend fun isFavorite(username: String): Boolean

    @Query("DELETE FROM userfaventity WHERE username = :username")
    suspend fun deleteByUsername(username: String)
}