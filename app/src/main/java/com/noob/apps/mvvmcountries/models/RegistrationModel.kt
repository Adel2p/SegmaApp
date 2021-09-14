package com.noob.apps.mvvmcountries.models

import java.util.*

data class RegistrationModel(
    val name: String,
    val email: String,
    val mobileNumber: String,
    val password: String,
    val gender: String,
    val os: String = "ANDROID",
    val deviceId: String,
    val language: String =  Locale.getDefault().displayLanguage.uppercase()
)
