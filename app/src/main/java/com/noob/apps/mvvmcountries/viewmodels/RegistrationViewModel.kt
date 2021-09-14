package com.noob.apps.mvvmcountries.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.noob.apps.mvvmcountries.interfaces.NetworkResponseCallback
import com.noob.apps.mvvmcountries.models.*
import com.noob.apps.mvvmcountries.repositories.RegistrationRepository
import com.noob.apps.mvvmcountries.utils.NetworkHelper

class RegistrationViewModel(private val app: Application) : AndroidViewModel(app) {
    private var user: MutableLiveData<RegistrationResponse> =
        MutableLiveData<RegistrationResponse>()
    private var otpResponse: MutableLiveData<OtpResponse> =
        MutableLiveData<OtpResponse>()

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

    fun verifyOtp(otpModel: OtpModel): MutableLiveData<OtpResponse> {
        if (NetworkHelper.isOnline(app.baseContext)) {
            mShowProgressBar.value = true
            otpResponse = mRepository.verifyOtp(otpModel, object : NetworkResponseCallback {
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
        return otpResponse
    }

    fun resendOtp(otpModel: ResendModel): MutableLiveData<OtpResponse> {
        if (NetworkHelper.isOnline(app.baseContext)) {
            mShowProgressBar.value = true
            otpResponse = mRepository.reSendOtp(otpModel, object : NetworkResponseCallback {
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
        return otpResponse
    }

}