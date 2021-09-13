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

    @GET("fields/{id}/levels")
    fun getLevels(
        @Header("Authorization") Authorization: String?,
        @Path("id") id: String
    ): Call<BoardingResponse>

    @GET("levels/{id}/departments")
    fun getDepartments(
        @Header("Authorization") Authorization: String?,
        @Path("id") id: String
    ): Call<BoardingResponse>


    @POST("students/onboard")
    fun postBoardingData(  @Header("Authorization") Authorization: String?,
        @Body boardingRequest: BoardingRequest
    ): Call<BoardingResponse>

}