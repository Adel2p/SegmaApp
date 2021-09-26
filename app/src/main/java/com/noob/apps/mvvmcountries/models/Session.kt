package com.noob.apps.mvvmcountries.models

import java.io.Serializable

data class Session(
    val lectureUuid: String,
    val studentUuid: String,
    val createdAt: String,
    val expiredAt: String,
    val expired: Boolean
) : Serializable
