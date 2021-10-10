package com.noob.apps.mvvmcountries.data

import com.noob.apps.mvvmcountries.models.User
import com.noob.apps.mvvmcountries.models.WatchedLectures

interface DatabaseHelper {

    suspend fun getUsers(): List<User>

    suspend fun insertAll(users: List<User>)

    suspend fun findByUserId(userId: String): List<User>

    suspend fun deleteAll()

    suspend fun insertLectures(lecture: List<WatchedLectures>)

    suspend fun getLectures(): List<WatchedLectures>

    suspend fun update(lecture: WatchedLectures)
}

