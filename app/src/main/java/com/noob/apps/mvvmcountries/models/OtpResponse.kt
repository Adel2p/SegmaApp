package com.noob.apps.mvvmcountries.models

data class OtpResponse(
    val status: String,
    val message: String,
    val errors: List<String>
)