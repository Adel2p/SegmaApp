package com.noob.apps.mvvmcountries.data

import com.noob.apps.mvvmcountries.models.User

class DatabaseHelperImpl(private val appDatabase: AppDatabase) : DatabaseHelper {

    override suspend fun getUsers(): List<User> = appDatabase.userDao().getAll()

    override suspend fun insertAll(users: List<User>) = appDatabase.userDao().insertAll(users)

    override suspend fun findByUserId(userId: String) = appDatabase.userDao().findByUserId(userId)

    override suspend fun deleteAll() = appDatabase.userDao().deleteAll()


}