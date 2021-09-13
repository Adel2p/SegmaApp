package com.noob.apps.mvvmcountries.models

import java.io.Serializable

data class BoardingRequest(
    val fieldUuid: String,
    val levelUuid: String,
    val departmentUuid: String
) : Serializable
