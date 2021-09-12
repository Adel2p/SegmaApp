package com.noob.apps.mvvmcountries.models

data class RegistrationModel(
    val name: String,
    val email: String,
    val mobileNumber: String,
    val password: String,
    val gender: String,
    val os: String="ANDROID",
    val deviceId: String,
  //  val language: String=if (Localization.isArabic()) "ARABIC" else "ENGLISH"
)
