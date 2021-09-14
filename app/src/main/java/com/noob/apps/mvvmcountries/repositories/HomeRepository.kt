package com.noob.apps.mvvmcountries.repositories

import androidx.lifecycle.MutableLiveData
import com.noob.apps.mvvmcountries.interfaces.NetworkResponseCallback
import com.noob.apps.mvvmcountries.models.DepartmentCourseResponse
import com.noob.apps.mvvmcountries.network.RestClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeRepository private constructor() {
    private lateinit var mCallback: NetworkResponseCallback
    private var depResponse: MutableLiveData<DepartmentCourseResponse> =
        MutableLiveData<DepartmentCourseResponse>()

    companion object {
        private var mInstance: HomeRepository? = null
        fun getInstance(): HomeRepository {
            if (mInstance == null) {
                synchronized(this) {
                    mInstance = HomeRepository()
                }
            }
            return mInstance!!
        }
    }


    private lateinit var mUserCall: Call<DepartmentCourseResponse>

    fun getDepartmentCourses(
        token: String,
        callback: NetworkResponseCallback,
    ): MutableLiveData<DepartmentCourseResponse> {
        mCallback = callback
        mUserCall = RestClient.getInstance().getApiService().getDepartmentCourses(token)
        mUserCall.enqueue(object : Callback<DepartmentCourseResponse> {

            override fun onResponse(
                call: Call<DepartmentCourseResponse>,
                response: Response<DepartmentCourseResponse>
            ) {
                if (response.code() != 200) {
                    val jsonObject: JSONObject?
                    jsonObject = JSONObject(response.errorBody()!!.string())
                    val userMessage = jsonObject.getString("error")
                    val internalMessage = jsonObject.getString("error_description")
                    mCallback.onResponseError(internalMessage)
                } else {
                    depResponse.value = response.body()
                    mCallback.onNetworkSuccess()
                }
            }

            override fun onFailure(call: Call<DepartmentCourseResponse>, t: Throwable) {
                mCallback.onNetworkFailure(t)
            }

        })
        return depResponse
    }
}