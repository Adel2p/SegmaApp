package com.noob.apps.mvvmcountries.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noob.apps.mvvmcountries.models.RegistrationResponse
import com.noob.apps.mvvmcountries.models.User
import com.noob.apps.mvvmcountries.models.WatchedLectures
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class RoomViewModel(private val dbHelper: DatabaseHelper) :
    ViewModel() {
    private var users: MutableLiveData<MutableList<User>> =
        MutableLiveData<MutableList<User>>()
    private var lectures: MutableLiveData<MutableList<WatchedLectures>> =
        MutableLiveData<MutableList<WatchedLectures>>()
    var lecturesToInsertInDB = mutableListOf<WatchedLectures>()
    var lecturesDB = mutableListOf<WatchedLectures>()
    private var user: MutableLiveData<MutableList<User>> =
        MutableLiveData<MutableList<User>>()
    private var inInserted: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>()
    var usersToInsertInDB = mutableListOf<User>()

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

    fun addLecture(watchedLectures: WatchedLectures) {
        lecturesToInsertInDB.add(watchedLectures)
        CoroutineScope(IO).launch {
            dbHelper.insertLectures(lecturesToInsertInDB)
        }
    }

    fun getLectures(): MutableLiveData<MutableList<WatchedLectures>> {
        CoroutineScope(IO).launch {
            lecturesDB = dbHelper.getLectures() as MutableList<WatchedLectures>
            lectures.postValue(lecturesDB)
        }
        return lectures

    }

    fun updateLecture(
        lecture: WatchedLectures
    ) {
        CoroutineScope(IO).launch {
            dbHelper.update(lecture)
        }

    }

    fun clearData(
    ) {
        CoroutineScope(IO).launch {
            dbHelper.deleteAll()
        }
    }

}

