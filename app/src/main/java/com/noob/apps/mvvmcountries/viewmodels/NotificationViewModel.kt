package com.noob.apps.mvvmcountries.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.noob.apps.mvvmcountries.interfaces.NetworkResponseCallback
import com.noob.apps.mvvmcountries.models.NotificationResponse
import com.noob.apps.mvvmcountries.repositories.NotificationRepository
import com.noob.apps.mvvmcountries.utils.NetworkHelper

class NotificationViewModel(private val app: Application) : AndroidViewModel(app) {
    private var notificationResponse: MutableLiveData<NotificationResponse> =
        MutableLiveData<NotificationResponse>()
    val mShowProgressBar = MutableLiveData(true)
    val mShowNetworkError: MutableLiveData<Boolean> = MutableLiveData()
    val mShowApiError = MutableLiveData<String>()
    val mShowResponseError = MutableLiveData<String>()
    private var mRepository = NotificationRepository.getInstance()


    fun getNotifications(token:String): MutableLiveData<NotificationResponse> {
        if (NetworkHelper.isOnline(app.baseContext)) {
            mShowProgressBar.value = true
            notificationResponse = mRepository.getNotifications(token,object : NetworkResponseCallback {
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
        return notificationResponse
    }
}