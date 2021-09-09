package com.noob.apps.mvvmcountries.repositories

import androidx.lifecycle.MutableLiveData
import com.noob.apps.mvvmcountries.interfaces.NetworkResponseCallback
import com.noob.apps.mvvmcountries.models.LoginResponse
import com.noob.apps.mvvmcountries.network.RestClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository private constructor() {
    private lateinit var mCallback: NetworkResponseCallback
    private var user: MutableLiveData<LoginResponse> =
        MutableLiveData<LoginResponse>()

    companion object {
        private var mInstance: LoginRepository? = null
        fun getInstance(): LoginRepository {
            if (mInstance == null) {
                synchronized(this) {
                    mInstance = LoginRepository()
                }
            }
            return mInstance!!
        }
    }


    private lateinit var mUserCall: Call<LoginResponse>

    fun getCountries(
        callback: NetworkResponseCallback,
    ): MutableLiveData<LoginResponse> {
        mCallback = callback
        if (user.value?.error?.isEmpty() == true) {
            mCallback.onNetworkSuccess()
            return user
        }
        mUserCall = RestClient.getInstance().getApiService().userLogin("01205459010", "123456")
        mUserCall.enqueue(object : Callback<LoginResponse> {

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                user.value = response.body()
                mCallback.onNetworkSuccess()
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                mCallback.onNetworkFailure(t)
            }

        })
        return user
    }
}