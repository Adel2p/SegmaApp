package com.noob.apps.mvvmcountries.data

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
    @Query("DELETE FROM User")
    fun deleteAll()
//    @Query("UPDATE orders SET order_amount = :amount, price = :price WHERE order_id =:id")
//    fun updateUserToken(user_uuid: String, token: String): List<User>
}