package com.noob.apps.mvvmcountries.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.noob.apps.mvvmcountries.interfaces.NetworkResponseCallback
import com.noob.apps.mvvmcountries.models.RegistrationModel
import com.noob.apps.mvvmcountries.models.RegistrationResponse
import com.noob.apps.mvvmcountries.repositories.RegistrationRepository
import com.noob.apps.mvvmcountries.utils.NetworkHelper

class RegistrationViewModel(private val app: Application) : AndroidViewModel(app) {
    private var user: MutableLiveData<RegistrationResponse> =
        MutableLiveData<RegistrationResponse>()
    val mShowProgressBar = MutableLiveData(true)
    val mShowNetworkError: MutableLiveData<Boolean> = MutableLiveData()
    val mShowApiError = MutableLiveData<String>()
    val mShowResponseError = MutableLiveData<String>()
    private var mRepository = RegistrationRepository.getInstance()

    fun register(registrationModel: RegistrationModel): MutableLiveData<RegistrationResponse> {
        if (NetworkHelper.isOnline(app.baseContext)) {
            mShowProgressBar.value = true
            user = mRepository.register(registrationModel, object : NetworkResponseCallback {
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

}