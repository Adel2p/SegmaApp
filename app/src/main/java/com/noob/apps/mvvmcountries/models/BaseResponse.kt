package com.noob.apps.mvvmcountries.models

import java.io.Serializable

data class BaseResponse(
    val status: String,
    val message: String
) : Serializable
