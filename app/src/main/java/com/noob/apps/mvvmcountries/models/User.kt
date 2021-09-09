package com.noob.apps.mvvmcountries.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("access_token") val access_token: String,
    @SerializedName("token_type") val token_type: String,
    @SerializedName("refresh_token") val refresh_token: String,
    @SerializedName("expires_in") val expires_in: Int,
    @SerializedName("scope") val scope: String,
    @SerializedName("user_uuid") val user_uuid: String,
    @SerializedName("user_email") val user_email: String,
    @SerializedName("user_on_boarded") val user_on_boarded: Boolean,
    @SerializedName("user_name") val user_name: String,
    @SerializedName("user_mobile_number") val user_mobile_number: Int,
    @SerializedName("user_device_id") val user_device_id: Int,
    @SerializedName("user_gender") val user_gender: String,
    @SerializedName("jti") val jti: String
)