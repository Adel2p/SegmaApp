package com.noob.apps.mvvmcountries.models

import java.io.Serializable

data class AttachmentResponse(
    val status : String,
     val message : String,
     val errors : String,
    val data : List<Attachments>
):Serializable
