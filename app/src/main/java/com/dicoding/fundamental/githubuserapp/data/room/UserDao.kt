package com.dicoding.fundamental.githubuserapp.data.room

import androidx.room.*
import com.dicoding.fundamental.githubuserapp.data.local.entity.FavoriteUserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM tbl_fav where favorite = 1")
    fun getFavoritedUser(): Flow<List<FavoriteUserEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: FavoriteUserEntity)

    @Delete
    suspend fun deleteUser(user: FavoriteUserEntity)

    @Query("SELECT EXISTS(SELECT * FROM tbl_fav WHERE login = :username AND favorite = 1)")
    suspend fun isUserFavorited(username: String): Boolean
}