package com.noob.apps.mvvmcountries.repositories

import androidx.lifecycle.MutableLiveData
import com.noob.apps.mvvmcountries.data.DatabaseHelper
import com.noob.apps.mvvmcountries.interfaces.NetworkResponseCallback
import com.noob.apps.mvvmcountries.models.LoginResponse
import com.noob.apps.mvvmcountries.models.User
import com.noob.apps.mvvmcountries.network.RestClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import org.json.JSONObject


class LoginRepository private constructor() {
    var usersToInsertInDB = mutableListOf<User>()

    private lateinit var mCallback: NetworkResponseCallback
    private var loginResponse: MutableLiveData<LoginResponse> =
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

    fun login(
        dbHelper: DatabaseHelper,
        mobile: String, password: String,
        callback: NetworkResponseCallback,
    ): MutableLiveData<LoginResponse> {
        mCallback = callback
        mUserCall = RestClient.getInstance().getApiService()
            .userLogin("Basic U2lnbWEtTW9iaWxlOjEyMzQ1Ng==", "password", mobile, password)
        mUserCall.enqueue(object : Callback<LoginResponse> {

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.code() != 200) {
                    val jsonObject: JSONObject?
                    jsonObject = JSONObject(response.errorBody()!!.string())
                    val userMessage = jsonObject.getString("error")
                    val internalMessage = jsonObject.getString("error_description")
                    mCallback.onResponseError(internalMessage)
                } else {
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
                        CoroutineScope(Dispatchers.IO).launch {
                            usersToInsertInDB = dbHelper.getUsers() as MutableList<User>
                        }

                    }
                    loginResponse.value = response.body()
                    mCallback.onNetworkSuccess()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                mCallback.onNetworkFailure(t)
            }

        })
        return loginResponse
    }
}