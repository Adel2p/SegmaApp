package com.noob.apps.mvvmcountries.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.noob.apps.mvvmcountries.models.BaseResponse
import com.noob.apps.mvvmcountries.repositories.RegistrationRepository

class ForgetPasswordViewModel(private val app: Application) : AndroidViewModel(app) {
    private var user: MutableLiveData<BaseResponse> =
        MutableLiveData<BaseResponse>()
    val mShowProgressBar = MutableLiveData(true)
    val mShowNetworkError: MutableLiveData<Boolean> = MutableLiveData()
    val mShowApiError = MutableLiveData<String>()
    val mShowResponseError = MutableLiveData<String>()
    private var mRepository = RegistrationRepository.getInstance()
}