package com.noob.apps.mvvmcountries.models

data class UserInfoResponse(
    val status: String,
    val message: String,
    val errors: String,
    val data: UserInfo
)
