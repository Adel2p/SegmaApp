package com.noob.apps.mvvmcountries.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.noob.apps.mvvmcountries.models.User

@Dao
interface UserDao {

    @Query("SELECT * FROM user")
    suspend fun getAll(): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<User>)

    @Query("SELECT * FROM user WHERE user_uuid LIKE :user_uuid")
    fun findByUserId(user_uuid: String): List<User>
}