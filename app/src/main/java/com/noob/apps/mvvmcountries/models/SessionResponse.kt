package com.noob.apps.mvvmcountries.models

import java.io.Serializable

data class SessionResponse(
     val status : String,
     val message : String,
   val errors : String,
    val data : Session
):Serializable
