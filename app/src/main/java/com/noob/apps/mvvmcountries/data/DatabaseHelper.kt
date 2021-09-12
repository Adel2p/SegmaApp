package com.noob.apps.mvvmcountries.data

import com.noob.apps.mvvmcountries.models.User

interface DatabaseHelper {

    suspend fun getUsers(): List<User>

    suspend fun insertAll(users: List<User>)

    suspend fun findByUserId(userId: String): List<User>
}

