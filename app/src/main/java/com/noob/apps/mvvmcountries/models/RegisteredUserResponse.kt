package com.noob.apps.mvvmcountries.models

import java.io.Serializable

data class RegisteredUserResponse(
    val uuid: String,
    val name: String,
    val mobileNumber: Int,
    val email: String,
    val gender: String,
    val enabled: Boolean,
    val verified: Boolean,
    val onBoarded: Boolean
) : Serializable
