package com.noob.apps.mvvmcountries.models

import com.google.gson.annotations.SerializedName

data class ResponseError(@SerializedName("error") val error : String,
                         @SerializedName("error_description") val error_description : String)
