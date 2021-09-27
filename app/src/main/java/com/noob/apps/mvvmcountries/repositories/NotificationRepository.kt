package com.noob.apps.mvvmcountries.repositories

import androidx.lifecycle.MutableLiveData
import com.noob.apps.mvvmcountries.interfaces.NetworkResponseCallback
import com.noob.apps.mvvmcountries.models.NotificationResponse
import com.noob.apps.mvvmcountries.network.RestClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationRepository private constructor() {
    private lateinit var mCallback: NetworkResponseCallback
    private var notificationResponse: MutableLiveData<NotificationResponse> =
        MutableLiveData<NotificationResponse>()

    companion object {
        private var mInstance: NotificationRepository? = null
        fun getInstance(): NotificationRepository {
            if (mInstance == null) {
                synchronized(this) {
                    mInstance = NotificationRepository()
                }
            }
            return mInstance!!
        }
    }

    private lateinit var mNotificationCall: Call<NotificationResponse>
    fun getNotifications(
        token: String,
        callback: NetworkResponseCallback,
    ): MutableLiveData<NotificationResponse> {
        mCallback = callback
        if (notificationResponse.value != null) {
            mCallback.onNetworkSuccess()
            notificationResponse = MutableLiveData()
        }
        mNotificationCall = RestClient.getInstance().getApiService()
            .getNotifications(token)
        mNotificationCall.enqueue(object : Callback<NotificationResponse> {
            override fun onResponse(
                call: Call<NotificationResponse>,
                response: Response<NotificationResponse>
            ) {
                if (response.code() != 200) {
                    val jsonObject: JSONObject?
                    jsonObject = JSONObject(response.errorBody()!!.string())
                    val userMessage = jsonObject.getString("error")
                    val internalMessage = jsonObject.getString("error_description")
                    mCallback.onResponseError(internalMessage)
                } else {
                    notificationResponse.value = response.body()
                    mCallback.onNetworkSuccess()
                }
            }

            override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                mCallback.onNetworkFailure(t)
            }

        })
        return notificationResponse
    }

}