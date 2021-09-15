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
    fun postBoardingData(
        @Header("Authorization") Authorization: String?,
        @Body boardingRequest: BoardingRequest
    ): Call<BoardingResponse>

    @POST("students/signup")
    fun userSignUp(
        @Body registrationModel: RegistrationModel
    ): Call<RegistrationResponse>

    @POST("students/verifyOtp")
    fun verifyOtp(
        @Body otpModel: OtpModel
    ): Call<OtpResponse>

    @POST("students/resendOtp")
    fun resendOtp(
        @Body resendModel: ResendModel
    ): Call<OtpResponse>

    @GET("departments/courses")
    fun getDepartmentCourses(@Header("Authorization") Authorization: String?): Call<DepartmentCourseResponse>

    @GET("students/info")
    fun getStudentInfo(@Header("Authorization") Authorization: String?): Call<UserInfoResponse>
    @FormUrlEncoded
    @POST("oauth/token")
    fun updateToken( @Header("Authorization") Authorization: String?,
                     @Field("grant_type") grant_type: String?,
                     @Field("refresh_token") refresh_token: String?,): Call<LoginResponse>
}