package com.noob.apps.mvvmcountries.models

import java.io.Serializable

data class LectureDetails(
    val uuid: String,
    val name: String,
    val url: String,
    val sessionTimeout: Int,
    val allowedSessions: Int,
    val actualSessions: Int,
    val resolutions: String,
    val publicWatch: Boolean,
    val studentSessions: List<StudentSessions>
) : Serializable
