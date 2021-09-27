package com.noob.apps.mvvmcountries.models

data class NotificationResponse(
     val status : String,
     val message : String,
   val errors : String,
    val data : List<Notification>
)