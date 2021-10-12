package com.noob.apps.mvvmcountries.models

data class UserInfo(
    val uuid: String,
    val name: String,
    val mobileNumber: Int,
    val email: String,
    val gender: String,
    val enabled: Boolean,
    val verified: Boolean,
    val onBoarded: Boolean,
    val levelName: String,
    val studyFieldName: String,
    val departmentName: String,
    val deviceId: String
)
