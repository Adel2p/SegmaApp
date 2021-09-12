package com.noob.apps.mvvmcountries.utils

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.noob.apps.mvvmcountries.data.DatabaseHelper
import com.noob.apps.mvvmcountries.data.RoomViewModel
import com.noob.apps.mvvmcountries.viewmodels.LoginViewModel


class ViewModelFactory(private val app: Application, private val dbHelper: DatabaseHelper) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {


        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(app, dbHelper) as T
        }
        if (modelClass.isAssignableFrom(RoomViewModel::class.java)) {
            return RoomViewModel(dbHelper) as T
        }

        throw IllegalArgumentException("Unknown class name")
    }

}