package com.noob.apps.mvvmcountries.network

import com.noob.apps.mvvmcountries.models.*
import retrofit2.Call
import retrofit2.http.*

interface ApiServices {

    @GET("all")
    fun getCountries(): Call<List<Country>>

    @FormUrlEncoded
    @POST("oauth/token")
    fun userLogin(
        @Header("Authorization") Authorization: String?,
        @Field("grant_type") grant_type: String?,
        @Field("username") username: String?,
        @Field("password") password: String?
    ): Call<LoginResponse>

    @POST("students/signu")
    fun userSignUp(
        @Body registrationModel: RegistrationModel
    ): Call<RegistrationResponse>

    @GET("category/UNIVERSITY/fields")
    fun getUNIVERSITY(@Header("Authorization") Authorization: String?): Call<BoardingResponse>

}