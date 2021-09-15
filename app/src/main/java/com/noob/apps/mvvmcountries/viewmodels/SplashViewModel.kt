package com.noob.apps.mvvmcountries.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.noob.apps.mvvmcountries.data.DatabaseHelper
import com.noob.apps.mvvmcountries.interfaces.NetworkResponseCallback
import com.noob.apps.mvvmcountries.models.LoginResponse
import com.noob.apps.mvvmcountries.models.RefreshTokenModel
import com.noob.apps.mvvmcountries.repositories.SplashRepository
import com.noob.apps.mvvmcountries.utils.NetworkHelper

class SplashViewModel(
    private val app: Application,
    private val dbHelper: DatabaseHelper
) : AndroidViewModel(app) {
    var updateTokenResponse: MutableLiveData<LoginResponse?> =
        MutableLiveData<LoginResponse?>()
    val mShowProgressBar = MutableLiveData(true)
    val mShowNetworkError: MutableLiveData<Boolean> = MutableLiveData()
    val mShowApiError = MutableLiveData<String>()
    val mShowResponseError = MutableLiveData<String>()
    private var mRepository = SplashRepository.getInstance()
    fun updateToken(refreshTokenModel: RefreshTokenModel) {
        if (NetworkHelper.isOnline(app.baseContext)) {
            mShowProgressBar.value = true
            updateTokenResponse =
                mRepository.updateTokenResponse(
                    dbHelper,
                    refreshTokenModel,
                    object : NetworkResponseCallback {
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