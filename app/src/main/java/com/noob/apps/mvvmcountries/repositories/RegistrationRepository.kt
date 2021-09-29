package com.noob.apps.mvvmcountries.repositories

import androidx.lifecycle.MutableLiveData
import com.noob.apps.mvvmcountries.interfaces.NetworkResponseCallback
import com.noob.apps.mvvmcountries.models.*
import com.noob.apps.mvvmcountries.network.RestClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationRepository private constructor() {
    private lateinit var mCallback: NetworkResponseCallback
    private var registrationResponse: MutableLiveData<RegistrationResponse> =
        MutableLiveData<RegistrationResponse>()
    private var otpResponse: MutableLiveData<OtpResponse> =
        MutableLiveData<OtpResponse>()

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
    private lateinit var mOtpCall: Call<OtpResponse>

    fun register(
        registrationModel: RegistrationModel,
        callback: NetworkResponseCallback,
    ): MutableLiveData<RegistrationResponse> {
        mCallback = callback
        if (registrationResponse.value != null) {
            mCallback.onNetworkSuccess()
            registrationResponse = MutableLiveData()
        }
        mUserCall = RestClient.getInstance().getApiService()
            .userSignUp(registrationModel)
        mUserCall.enqueue(object : Callback<RegistrationResponse> {
            override fun onResponse(
                call: Call<RegistrationResponse>,
                response: Response<RegistrationResponse>
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
                    registrationResponse.value = response.body()
                    mCallback.onNetworkSuccess()
                }
            }

            override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
                mCallback.onNetworkFailure(t)
            }

        })
        return registrationResponse
    }

    fun verifyOtp(
        otpModel: OtpModel,
        callback: NetworkResponseCallback,
    ): MutableLiveData<OtpResponse> {
        mCallback = callback
        if (otpResponse.value != null) {
            mCallback.onNetworkSuccess()
            otpResponse = MutableLiveData()
        }
        mOtpCall = RestClient.getInstance().getApiService()
            .verifyOtp(otpModel)
        mOtpCall.enqueue(object : Callback<OtpResponse> {

            override fun onResponse(
                call: Call<OtpResponse>,
                response: Response<OtpResponse>
            ) {
                if (response.code() != 200) {
                    val jsonObject: JSONObject?
                    jsonObject = JSONObject(response.errorBody()!!.string())
                    val userMessage = jsonObject.getString("message")
                    val internalMessage = jsonObject.getString("message")
                    mCallback.onResponseError(internalMessage)
                } else {
                    otpResponse.value = response.body()
                    mCallback.onNetworkSuccess()
                }
            }

            override fun onFailure(call: Call<OtpResponse>, t: Throwable) {
                mCallback.onNetworkFailure(t)
            }

        })
        return otpResponse
    }

    fun reSendOtp(
        otpModel: ResendModel,
        callback: NetworkResponseCallback,
    ): MutableLiveData<OtpResponse> {
        mCallback = callback
        if (otpResponse.value != null) {
            mCallback.onNetworkSuccess()
            otpResponse = MutableLiveData()
        }
        mOtpCall = RestClient.getInstance().getApiService()
            .resendOtp(otpModel)
        mOtpCall.enqueue(object : Callback<OtpResponse> {

            override fun onResponse(
                call: Call<OtpResponse>,
                response: Response<OtpResponse>
            ) {
                if (response.code() != 200) {
                    val jsonObject: JSONObject?
                    jsonObject = JSONObject(response.errorBody()!!.string())
                    val userMessage = jsonObject.getString("message")
                    val internalMessage = jsonObject.getString("message")
                    mCallback.onResponseError(internalMessage)
                } else {
                    otpResponse.value = response.body()
                    mCallback.onNetworkSuccess()
                }
            }

            override fun onFailure(call: Call<OtpResponse>, t: Throwable) {
                mCallback.onNetworkFailure(t)
            }

        })
        return otpResponse
    }
}