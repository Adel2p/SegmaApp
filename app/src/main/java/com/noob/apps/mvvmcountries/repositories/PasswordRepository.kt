package com.noob.apps.mvvmcountries.repositories

import androidx.lifecycle.MutableLiveData
import com.noob.apps.mvvmcountries.interfaces.NetworkResponseCallback
import com.noob.apps.mvvmcountries.models.BaseResponse
import com.noob.apps.mvvmcountries.models.ForgetPasswordModel
import com.noob.apps.mvvmcountries.models.RegistrationModel
import com.noob.apps.mvvmcountries.models.RegistrationResponse
import com.noob.apps.mvvmcountries.network.RestClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PasswordRepository private constructor() {
    private lateinit var mCallback: NetworkResponseCallback
    private var passwordResponse: MutableLiveData<BaseResponse> =
        MutableLiveData<BaseResponse>()
    private lateinit var mUserCall: Call<BaseResponse>

    companion object {
        private var mInstance: PasswordRepository? = null
        fun getInstance(): PasswordRepository {
            if (mInstance == null) {
                synchronized(this) {
                    mInstance = PasswordRepository()
                }
            }
            return mInstance!!
        }
    }
    fun forgetPassword(
        registrationModel: ForgetPasswordModel,
        callback: NetworkResponseCallback,
    ): MutableLiveData<BaseResponse> {
        mCallback = callback
        if (passwordResponse.value != null) {
            mCallback.onNetworkSuccess()
            passwordResponse = MutableLiveData()
        }
        mUserCall = RestClient.getInstance().getApiService()
            .forgetPassword(registrationModel)
        mUserCall.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                if (response.code() == 400) {
                    val jsonObject: JSONObject?
                    jsonObject = JSONObject(response.errorBody()!!.string())
                    val internalMessage = jsonObject.getString("message")
                    mCallback.onResponseError(internalMessage)
                } else if (response.code() != 201) {
                    val jsonObject: JSONObject?
                    jsonObject = JSONObject(response.errorBody()!!.string())
                    val userMessage = jsonObject.getString("error")
                    val internalMessage = jsonObject.getString("error_description")
                    mCallback.onResponseError(internalMessage)
                } else {
                    passwordResponse.value = response.body()
                    mCallback.onNetworkSuccess()
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                mCallback.onNetworkFailure(t)
            }

        })
        return passwordResponse
    }

}