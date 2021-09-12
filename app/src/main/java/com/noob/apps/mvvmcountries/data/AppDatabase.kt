package com.noob.apps.mvvmcountries.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.noob.apps.mvvmcountries.models.User

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

}