package com.noob.apps.mvvmcountries.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.noob.apps.mvvmcountries.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class RoomViewModel(private val dbHelper: DatabaseHelper) :
    ViewModel() {
    private var users: MutableLiveData<MutableList<User>> =
        MutableLiveData<MutableList<User>>()
    private var user: MutableLiveData<MutableList<User>> =
        MutableLiveData<MutableList<User>>()

    fun getUsers(
    ): MutableLiveData<MutableList<User>> {
        var usersToInsertInDB: MutableList<User>
        CoroutineScope(IO).launch {
            usersToInsertInDB = dbHelper.getUsers() as MutableList<User>
            users.postValue(usersToInsertInDB)
        }

        return users
    }

    fun updateUserToken(
        userId: String
    ) {
        var usersToInsertInDB: MutableList<User>
        CoroutineScope(IO).launch {
            usersToInsertInDB = dbHelper.getUsers() as MutableList<User>
        }

    }

    fun findUser(
        userId: String
    ): MutableLiveData<MutableList<User>> {
        var usersToInsertInDB: MutableList<User>? = null
        CoroutineScope(IO).launch {
            usersToInsertInDB = dbHelper.findByUserId(userId) as MutableList<User>
            user.postValue(usersToInsertInDB!!)
        }



        return user
    }
}

