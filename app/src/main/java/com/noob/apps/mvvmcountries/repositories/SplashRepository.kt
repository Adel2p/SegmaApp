package com.noob.apps.mvvmcountries.repositories

import androidx.lifecycle.MutableLiveData
import com.noob.apps.mvvmcountries.data.DatabaseHelper
import com.noob.apps.mvvmcountries.interfaces.NetworkResponseCallback
import com.noob.apps.mvvmcountries.models.LoginResponse
import com.noob.apps.mvvmcountries.models.RefreshTokenModel
import com.noob.apps.mvvmcountries.models.User
import com.noob.apps.mvvmcountries.network.RestClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashRepository private constructor() {
    private lateinit var mCallback: NetworkResponseCallback
    var updateTokenResponse: MutableLiveData<LoginResponse?> =
        MutableLiveData<LoginResponse?>()
    private var usersToInsertInDB = mutableListOf<User>()
    private lateinit var mTokenCall: Call<LoginResponse>

    companion object {
        private var mInstance: SplashRepository? = null
        fun getInstance(): SplashRepository {
            if (mInstance == null) {
                synchronized(this) {
                    mInstance = SplashRepository()
                }
            }
            return mInstance!!
        }
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

}