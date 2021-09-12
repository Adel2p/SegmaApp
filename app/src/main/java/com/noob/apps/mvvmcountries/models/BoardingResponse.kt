package com.noob.apps.mvvmcountries.models

data class BoardingResponse(

    val status: String,
    val message: String,
    val errors: String,
    val data: List<Collage>
)