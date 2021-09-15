package com.noob.apps.mvvmcountries.models

data class RefreshTokenModel(
    val grant_type: String,
    val refresh_token: String
)
