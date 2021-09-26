package com.noob.apps.mvvmcountries.repositories

import androidx.lifecycle.MutableLiveData
import com.noob.apps.mvvmcountries.data.DatabaseHelper
import com.noob.apps.mvvmcountries.interfaces.NetworkResponseCallback
import com.noob.apps.mvvmcountries.models.*
import com.noob.apps.mvvmcountries.network.RestClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeRepository private constructor() {
    private lateinit var mCallback: NetworkResponseCallback
    private var depResponse: MutableLiveData<DepartmentCourseResponse?> =
        MutableLiveData<DepartmentCourseResponse?>()
    var infoResponse: MutableLiveData<UserInfoResponse?> =
        MutableLiveData<UserInfoResponse?>()
    var updateTokenResponse: MutableLiveData<LoginResponse?> =
        MutableLiveData<LoginResponse?>()
    var fcmResponse: MutableLiveData<BaseResponse?> =
        MutableLiveData<BaseResponse?>()
    var lectureResponse: MutableLiveData<LectureDetailsResponse?> =
        MutableLiveData<LectureDetailsResponse?>()
    var sessionResponse: MutableLiveData<SessionResponse?> =
        MutableLiveData<SessionResponse?>()
    private lateinit var mUserCall: Call<DepartmentCourseResponse>
    private lateinit var mInfoCall: Call<UserInfoResponse>
    private lateinit var mTokenCall: Call<LoginResponse>
    private lateinit var fcmTokenCall: Call<BaseResponse>
    private lateinit var lecInfoCall: Call<LectureDetailsResponse>
    private lateinit var addSessionCall: Call<SessionResponse>

    var usersToInsertInDB = mutableListOf<User>()

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


    fun getDepartmentCourses(
        token: String,
        callback: NetworkResponseCallback,
    ): MutableLiveData<DepartmentCourseResponse?> {
        mCallback = callback
        if (depResponse.value != null) {
            mCallback.onNetworkSuccess()
            depResponse = MutableLiveData()
        }
        mUserCall = RestClient.getInstance().getApiService().getDepartmentCourses(token)
        mUserCall.enqueue(object : Callback<DepartmentCourseResponse> {

            override fun onResponse(
                call: Call<DepartmentCourseResponse>,
                response: Response<DepartmentCourseResponse>
            ) {
                if (response.code() != 200) {
                    val jsonObject: JSONObject?
                    jsonObject = JSONObject(response.errorBody()!!.string())
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

    fun getStudentInfo(
        token: String,
        callback: NetworkResponseCallback,
    ): MutableLiveData<UserInfoResponse?> {
        mCallback = callback
        if (infoResponse.value != null) {
            mCallback.onNetworkSuccess()
            infoResponse = MutableLiveData()
        }
        mInfoCall = RestClient.getInstance().getApiService().getStudentInfo(token)
        mInfoCall.enqueue(object : Callback<UserInfoResponse> {

            override fun onResponse(
                call: Call<UserInfoResponse>,
                response: Response<UserInfoResponse>
            ) {
                if (response.code() != 200) {
                    val jsonObject: JSONObject?
                    jsonObject = JSONObject(response.errorBody()!!.string())
                    val userMessage = jsonObject.getString("error")
                    val internalMessage = jsonObject.getString("error_description")
                    mCallback.onResponseError(internalMessage)
                } else {
                    infoResponse.value = response.body()
                    mCallback.onNetworkSuccess()
                }
            }

            override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                mCallback.onNetworkFailure(t)
            }

        })
        return infoResponse
    }

    fun updateTokenResponse(
        dbHelper: DatabaseHelper,
        refreshTokenModel: RefreshTokenModel,
        callback: NetworkResponseCallback,
    ): MutableLiveData<LoginResponse?> {
        mCallback = callback
        if (updateTokenResponse.value != null) {
            mCallback.onNetworkSuccess()
            updateTokenResponse = MutableLiveData()
        }
        mTokenCall = RestClient.getInstance().getApiService()
            .updateToken(
                "Basic U2lnbWEtTW9iaWxlOjEyMzQ1Ng==",
                refreshTokenModel.grant_type,
                refreshTokenModel.refresh_token
            )
        mTokenCall.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.code() != 200) {
                    val jsonObject: JSONObject?
                    jsonObject = JSONObject(response.errorBody()!!.string())
                    val userMessage = jsonObject.getString("error")
                    val internalMessage = jsonObject.getString("error_description")
                    mCallback.onResponseError(internalMessage)
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        dbHelper.deleteAll()
                    }
                    val user =
                        response.body()?.user_uuid?.let {
                            User(
                                it,
                                response.body()?.access_token,
                                response.body()?.token_type,
                                response.body()?.refresh_token,
                                response.body()?.expires_in,
                                response.body()?.scope,
                                response.body()?.user_email,
                                response.body()?.user_on_boarded,
                                response.body()?.user_name,
                                response.body()?.user_mobile_number,
                                response.body()?.user_device_id,
                                response.body()!!.user_gender,
                                response.body()!!.jti
                            )
                        }
                    if (user != null) {
                        usersToInsertInDB.add(user)
                        CoroutineScope(Dispatchers.IO).launch {
                            dbHelper.insertAll(usersToInsertInDB)
                        }
                    }
                    updateTokenResponse.value = response.body()
                    mCallback.onNetworkSuccess()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                mCallback.onNetworkFailure(t)
            }

        })
        return updateTokenResponse
    }

    fun updateFCMToken(
        token: String, fcmToken: String,
        callback: NetworkResponseCallback,
    ): MutableLiveData<BaseResponse?> {
        mCallback = callback
        if (fcmResponse.value != null) {
            mCallback.onNetworkSuccess()
            fcmResponse = MutableLiveData()
        }
        fcmTokenCall = RestClient.getInstance().getApiService()
            .updateFCMToken(
                token,
                FcmTokenModel(fcmToken)
            )
        fcmTokenCall.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                if (response.code() != 200) {
                    val jsonObject: JSONObject?
                    jsonObject = JSONObject(response.errorBody()!!.string())
                    val userMessage = jsonObject.getString("error")
                    val internalMessage = jsonObject.getString("error_description")
                    mCallback.onResponseError(internalMessage)
                } else {
                    fcmResponse.value = response.body()
                    mCallback.onNetworkSuccess()
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                mCallback.onNetworkFailure(t)
            }

        })
        return fcmResponse
    }

    fun getLectureInfo(
        token: String, lecId: String,
        callback: NetworkResponseCallback,
    ): MutableLiveData<LectureDetailsResponse?> {
        mCallback = callback
        if (fcmResponse.value != null) {
            mCallback.onNetworkSuccess()
            lectureResponse = MutableLiveData()
        }
        lecInfoCall = RestClient.getInstance().getApiService()
            .getLectureInfo(
                token,
                lecId
            )
        lecInfoCall.enqueue(object : Callback<LectureDetailsResponse> {
            override fun onResponse(
                call: Call<LectureDetailsResponse>,
                response: Response<LectureDetailsResponse>
            ) {
                if (response.code() != 200) {
                    val jsonObject: JSONObject?
                    jsonObject = JSONObject(response.errorBody()!!.string())
                    val userMessage = jsonObject.getString("error")
                    val internalMessage = jsonObject.getString("error_description")
                    mCallback.onResponseError(internalMessage)
                } else {
                    lectureResponse.value = response.body()
                    mCallback.onNetworkSuccess()
                }
            }

            override fun onFailure(call: Call<LectureDetailsResponse>, t: Throwable) {
                mCallback.onNetworkFailure(t)
            }

        })
        return lectureResponse
    }

    fun addSession(
        token: String, lecId: String,
        callback: NetworkResponseCallback,
    ): MutableLiveData<SessionResponse?> {
        mCallback = callback
        if (sessionResponse.value != null) {
            mCallback.onNetworkSuccess()
            sessionResponse = MutableLiveData()
        }
        addSessionCall = RestClient.getInstance().getApiService()
            .addSession(
                token,
                lecId
            )
        addSessionCall.enqueue(object : Callback<SessionResponse> {
            override fun onResponse(
                call: Call<SessionResponse>,
                response: Response<SessionResponse>
            ) {
                if (response.code() != 200) {
                    val jsonObject: JSONObject?
                    jsonObject = JSONObject(response.errorBody()!!.string())
                    val userMessage = jsonObject.getString("error")
                    val internalMessage = jsonObject.getString("error_description")
                    mCallback.onResponseError(internalMessage)
                } else {
                    sessionResponse.value = response.body()
                    mCallback.onNetworkSuccess()
                }
            }

            override fun onFailure(call: Call<SessionResponse>, t: Throwable) {
                mCallback.onNetworkFailure(t)
            }

        })
        return sessionResponse
    }
}