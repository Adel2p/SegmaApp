package com.noob.apps.mvvmcountries.repositories

import androidx.lifecycle.MutableLiveData
import com.noob.apps.mvvmcountries.interfaces.NetworkResponseCallback
import com.noob.apps.mvvmcountries.models.RegistrationModel
import com.noob.apps.mvvmcountries.models.RegistrationResponse
import com.noob.apps.mvvmcountries.network.RestClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationRepository private constructor() {
    private lateinit var mCallback: NetworkResponseCallback
    private var registrationResponse: MutableLiveData<RegistrationResponse> =
        MutableLiveData<RegistrationResponse>()

    companion object {
        private var mInstance: RegistrationRepository? = null
        fun getInstance(): RegistrationRepository {
            if (mInstance == null) {
                synchronized(this) {
                    mInstance = RegistrationRepository()
                }
            }
            return mInstance!!
        }
    }


    private lateinit var mUserCall: Call<RegistrationResponse>

    fun register(
        registrationModel: RegistrationModel,
        callback: NetworkResponseCallback,
    ): MutableLiveData<RegistrationResponse> {
        mCallback = callback
        mUserCall = RestClient.getInstance().getApiService()
            .userSignUp(registrationModel)
        mUserCall.enqueue(object : Callback<RegistrationResponse> {

            override fun onResponse(
                call: Call<RegistrationResponse>,
                response: Response<RegistrationResponse>
            ) {
                if (response.code() != 200) {
                    val jsonObject: JSONObject?
                    jsonObject = JSONObject(response.errorBody()!!.string())
                    val userMessage = jsonObject.getString("message")
                    val internalMessage = jsonObject.getString("message")
                    mCallback.onResponseError(internalMessage)
                } else {
                    registrationResponse.value = response.body()
                    registrationResponse.value?.isSuccess = true
                    mCallback.onNetworkSuccess()
                }
            }

            override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
                mCallback.onNetworkFailure(t)
            }

        })
        return registrationResponse
    }
}