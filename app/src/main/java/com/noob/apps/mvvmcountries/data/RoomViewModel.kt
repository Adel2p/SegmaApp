package com.noob.apps.mvvmcountries.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.noob.apps.mvvmcountries.models.RegistrationResponse
import com.noob.apps.mvvmcountries.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class RoomViewModel(private val dbHelper: DatabaseHelper) :
    ViewModel() {
    private var users: MutableLiveData<MutableList<User>> =
        MutableLiveData<MutableList<User>>()
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
//
//    fun insertUser(
//        response: RegistrationResponse
//    ): MutableLiveData<Boolean> {
//        val user =
//            response.user_uuid?.let {
//                User(
//                    it,
//                    response.access_token,
//                    response.token_type,
//                    response.refresh_token,
//                    response.expires_in,
//                    response.scope,
//                    response.user_email,
//                    response.user_on_boarded,
//                    response.user_name,
//                    response.user_mobile_number,
//                    response.user_device_id,
//                    response!!.user_gender,
//                    response!!.jti
//                )
//            }
//        if (user != null) {
//            usersToInsertInDB.add(user)
//            CoroutineScope(IO).launch {
//                dbHelper.insertAll(usersToInsertInDB)
//            }
//            inInserted.postValue(true)
//        }
//        return inInserted
//    }
}

