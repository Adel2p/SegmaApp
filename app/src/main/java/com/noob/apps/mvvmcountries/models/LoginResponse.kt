package com.noob.apps.mvvmcountries.models


data class LoginResponse(
    val error: String?,
    val error_description: String?,
    val user: User?,
)
