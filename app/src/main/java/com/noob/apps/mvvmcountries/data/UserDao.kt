package com.noob.apps.mvvmcountries.data

import androidx.room.*
import com.noob.apps.mvvmcountries.models.User
import com.noob.apps.mvvmcountries.models.WatchedLectures

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLectures(lecture: List<WatchedLectures>)

    @Query("SELECT * FROM WatchedLectures")
    suspend fun getAllLectures(): List<WatchedLectures>

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(lecture: WatchedLectures)
//    @Query("UPDATE orders SET order_amount = :amount, price = :price WHERE order_id =:id")
//    fun updateUserToken(user_uuid: String, token: String): List<User>
}