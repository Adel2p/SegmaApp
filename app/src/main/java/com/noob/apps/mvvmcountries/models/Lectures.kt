package com.noob.apps.mvvmcountries.models

import java.io.Serializable

data class Lectures(
    val uuid: String,
    val name: String,
    val url: String,
    val allowedWatches: Int,
    val resolutions: String,
    val actualWatches: Int,
    val totalWatches: Int
) : Serializable
