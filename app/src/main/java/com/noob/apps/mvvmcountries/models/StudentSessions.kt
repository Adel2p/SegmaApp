package com.noob.apps.mvvmcountries.models

import java.io.Serializable

data class StudentSessions(
    val createdAt: String,
    val expiredAt: String,
    val expired: Boolean
) : Serializable
