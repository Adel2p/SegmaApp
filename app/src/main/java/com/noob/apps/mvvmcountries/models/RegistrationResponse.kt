package com.noob.apps.mvvmcountries.models

import java.io.Serializable

data class RegistrationResponse(
    val status: String,
    val message: String,
    val data: RegisteredUserResponse
) : Serializable
