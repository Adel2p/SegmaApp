package com.noob.apps.mvvmcountries.repositories

import androidx.lifecycle.MutableLiveData
import com.noob.apps.mvvmcountries.interfaces.NetworkResponseCallback
import com.noob.apps.mvvmcountries.models.BoardingResponse
import com.noob.apps.mvvmcountries.network.RestClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UniversityRepository private constructor() {
    private lateinit var mCallback: NetworkResponseCallback
    private var mmUniversityList: MutableLiveData<BoardingResponse> =
        MutableLiveData<BoardingResponse>()

    companion object {
        private var mInstance: UniversityRepository? = null
        fun getInstance(): UniversityRepository {
            if (mInstance == null) {
                synchronized(this) {
                    mInstance = UniversityRepository()
                }
            }
            return mInstance!!
        }
    }


    private lateinit var mUniversityCall: Call<BoardingResponse>

    fun getUNIVERSITY(
        token: String,
        callback: NetworkResponseCallback
    ): MutableLiveData<BoardingResponse> {
        mCallback = callback
        mUniversityCall = RestClient.getInstance().getApiService().getUNIVERSITY(token)
        mUniversityCall.enqueue(object : Callback<BoardingResponse> {

            override fun onResponse(
                call: Call<BoardingResponse>,
                response: Response<BoardingResponse>
            ) {
                mmUniversityList.value = response.body()
                mCallback.onNetworkSuccess()
            }

            override fun onFailure(call: Call<BoardingResponse>, t: Throwable) {
                mCallback.onNetworkFailure(t)
            }

        })
        return mmUniversityList
    }
}