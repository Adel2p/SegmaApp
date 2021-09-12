package com.noob.apps.mvvmcountries.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey
    var user_uuid: String,
    @ColumnInfo(name = "access_token") val access_token: String?,
    @ColumnInfo(name = "token_type") val token_type: String?,
    @ColumnInfo(name = "refresh_token") val refresh_token: String?,
    @ColumnInfo(name = "expires_in") val expires_in: String?,
    @ColumnInfo(name = "scope") val scope: String?,
    @ColumnInfo(name = "user_email") val user_email: String?,
    @ColumnInfo(name = "user_on_boarded") val user_on_boarded: Boolean?,
    @ColumnInfo(name = "user_name") val user_name: String?,
    @ColumnInfo(name = "user_mobile_number") val user_mobile_number: String?,
    @ColumnInfo(name = "user_device_id") val user_device_id: String?,
    @ColumnInfo(name = "user_gender") val user_gender: String?,
    @ColumnInfo(name = "jti") val jti: String?
)