package com.noob.apps.mvvmcountries.data

import com.noob.apps.mvvmcountries.models.User

interface ApiHelper {

    suspend fun getUsers(): List<User>

    suspend fun getMoreUsers(): List<User>

    suspend fun getUsersWithError(): List<User>
}