package com.noob.apps.mvvmcountries.models

import java.io.Serializable

data class Lectures(
    val uuid: String,
    val name: String,
    val url: String,
    val sessionTimeout: Int,
    val allowedSessions: Int,
    val allowedWatches: Int,
    val resolutions: String,
    val publicWatch: Boolean,
    val studentSessions: List<StudentSessions>
) : Serializable
