package com.noob.apps.mvvmcountries.network

import com.noob.apps.mvvmcountries.models.Country
import com.noob.apps.mvvmcountries.models.LoginResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiServices {

    @GET("all")
    fun getCountries(): Call<List<Country>>

    @FormUrlEncoded
    @POST("oauth/token")
    fun userLogin(
        @Field("username") username: String?,
        @Field("password") password: String?
    ): Call<LoginResponse>

}