package com.noob.apps.mvvmcountries.models

import java.io.Serializable

data class Course(
    val uuid: String,
    val name: String,
    val introUrl: String,
    val price: Int,
    val professor: Professor,
    val lectures: List<Lectures>, val eligibleToWatch: Boolean
) : Serializable
