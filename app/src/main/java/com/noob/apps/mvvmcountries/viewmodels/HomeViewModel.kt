package com.noob.apps.mvvmcountries.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.noob.apps.mvvmcountries.interfaces.NetworkResponseCallback
import com.noob.apps.mvvmcountries.models.DepartmentCourseResponse
import com.noob.apps.mvvmcountries.repositories.HomeRepository
import com.noob.apps.mvvmcountries.utils.NetworkHelper

class HomeViewModel(
    private val app: Application
) : AndroidViewModel(app) {
    private var depResponse: MutableLiveData<DepartmentCourseResponse> =
        MutableLiveData<DepartmentCourseResponse>()
    val mShowProgressBar = MutableLiveData(true)
    val mShowNetworkError: MutableLiveData<Boolean> = MutableLiveData()
    val mShowApiError = MutableLiveData<String>()
    val mShowResponseError = MutableLiveData<String>()
    private var mRepository = HomeRepository.getInstance()

    fun getDepartmentCourses(token: String): MutableLiveData<DepartmentCourseResponse> {
        if (NetworkHelper.isOnline(app.baseContext)) {
            mShowProgressBar.value = true
            depResponse =
                mRepository.getDepartmentCourses(token, object : NetworkResponseCallback {
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
        return depResponse
    }
}