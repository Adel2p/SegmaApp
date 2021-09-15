package com.noob.apps.mvvmcountries.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.noob.apps.mvvmcountries.data.DatabaseHelper
import com.noob.apps.mvvmcountries.interfaces.NetworkResponseCallback
import com.noob.apps.mvvmcountries.models.*
import com.noob.apps.mvvmcountries.repositories.HomeRepository
import com.noob.apps.mvvmcountries.utils.NetworkHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CourseViewModel(
    private val app: Application,
    private val dbHelper: DatabaseHelper
) : AndroidViewModel(app) {
    private var user: MutableLiveData<MutableList<User>> =
        MutableLiveData<MutableList<User>>()
    var depResponse: MutableLiveData<DepartmentCourseResponse?> =
        MutableLiveData<DepartmentCourseResponse?>()
    var infoResponse: MutableLiveData<UserInfoResponse?> =
        MutableLiveData<UserInfoResponse?>()
    var updateTokenResponse: MutableLiveData<LoginResponse?> =
        MutableLiveData<LoginResponse?>()
    var fcmResponse: MutableLiveData<BaseResponse?> =
        MutableLiveData<BaseResponse?>()
    val mShowProgressBar = MutableLiveData(true)
    val mShowNetworkError: MutableLiveData<Boolean> = MutableLiveData()
    val mShowApiError = MutableLiveData<String>()
    val mShowResponseError = MutableLiveData<String>()
    private var mRepository = HomeRepository.getInstance()

    fun findUser(
        userId: String
    ): MutableLiveData<MutableList<User>> {
        var usersToInsertInDB: MutableList<User>?
        CoroutineScope(Dispatchers.IO).launch {
            usersToInsertInDB = dbHelper.findByUserId(userId) as MutableList<User>
            user.postValue(usersToInsertInDB!!)
        }
        return user
    }

    fun getDepartmentCourses(token: String) {
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

    fun updateFCMToken(token: String, fcmToken: String) {
        if (NetworkHelper.isOnline(app.baseContext)) {
            mShowProgressBar.value = true
            fcmResponse =
                mRepository.updateFCMToken(token,
                    fcmToken,
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