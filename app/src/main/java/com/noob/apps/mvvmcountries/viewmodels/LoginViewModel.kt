package com.noob.apps.mvvmcountries.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.noob.apps.mvvmcountries.data.DatabaseHelper
import com.noob.apps.mvvmcountries.interfaces.NetworkResponseCallback
import com.noob.apps.mvvmcountries.models.LoginResponse
import com.noob.apps.mvvmcountries.models.UserInfoResponse
import com.noob.apps.mvvmcountries.repositories.LoginRepository
import com.noob.apps.mvvmcountries.utils.NetworkHelper

class LoginViewModel(
    private val app: Application,
    private val dbHelper: DatabaseHelper
) : AndroidViewModel(app) {
    private var user: MutableLiveData<LoginResponse> =
        MutableLiveData<LoginResponse>()
    var infoResponse: MutableLiveData<UserInfoResponse?> =
        MutableLiveData<UserInfoResponse?>()
    val mShowProgressBar = MutableLiveData(true)
    val mShowNetworkError: MutableLiveData<Boolean> = MutableLiveData()
    val mShowApiError = MutableLiveData<String>()
    val mShowResponseError = MutableLiveData<String>()
    private var mRepository = LoginRepository.getInstance()

    fun fetchCountriesFromServer(mobile: String, password: String): MutableLiveData<LoginResponse> {
        if (NetworkHelper.isOnline(app.baseContext)) {
            mShowProgressBar.value = true
            user = mRepository.login(dbHelper, mobile, password, object : NetworkResponseCallback {
                override fun onNetworkFailure(th: Throwable) {
                    mShowApiError.value = th.message
                }

                override fun onNetworkSuccess() {
                    mShowProgressBar.value = false
                }

                override fun onResponseError(message: String) {
                    mShowProgressBar.value = false
                    mShowResponseError.value = message
                }

            })
        } else {
            mShowProgressBar.value = false
            mShowNetworkError.value = true
        }
        return user
    }
    fun getStudentInfo(token: String) {
        if (NetworkHelper.isOnline(app.baseContext)) {
            mShowProgressBar.value = true
            infoResponse =
                mRepository.getStudentInfo(token, object : NetworkResponseCallback {
                    override fun onNetworkFailure(th: Throwable) {
                        mShowApiError.value = th.message
                    }

                    override fun onNetworkSuccess() {
                        mShowProgressBar.value = false
                    }

                    override fun onResponseError(message: String) {
                        mShowProgressBar.value = false
                        mShowResponseError.value = message
                    }

                })
        } else {
            mShowProgressBar.value = false
            mShowNetworkError.value = true
        }
    }

}