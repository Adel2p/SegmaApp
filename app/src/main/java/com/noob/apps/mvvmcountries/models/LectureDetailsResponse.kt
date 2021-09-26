package com.noob.apps.mvvmcountries.models

import java.io.Serializable

data class LectureDetailsResponse(
    val status: String,
    val message: String,
    val errors: String,
    val data: LectureDetails
):Serializable
