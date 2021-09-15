package com.noob.apps.mvvmcountries.utils

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.noob.apps.mvvmcountries.data.DatabaseHelper
import com.noob.apps.mvvmcountries.data.RoomViewModel
import com.noob.apps.mvvmcountries.viewmodels.CourseViewModel
import com.noob.apps.mvvmcountries.viewmodels.LoginViewModel
import com.noob.apps.mvvmcountries.viewmodels.SplashViewModel


class ViewModelFactory(private val app: Application, private val dbHelper: DatabaseHelper) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {


        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(app, dbHelper) as T
        }
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(app, dbHelper) as T
        }
        if (modelClass.isAssignableFrom(CourseViewModel::class.java)) {
            return CourseViewModel(app, dbHelper) as T
        }
        if (modelClass.isAssignableFrom(RoomViewModel::class.java)) {
            return RoomViewModel(dbHelper) as T
        }

        throw IllegalArgumentException("Unknown class name")
    }

}