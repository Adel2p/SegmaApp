package com.noob.apps.mvvmcountries.models

import java.io.Serializable

data class Attachments(
    val uuid: String,
    val name: String,
    val url: String
) : Serializable
