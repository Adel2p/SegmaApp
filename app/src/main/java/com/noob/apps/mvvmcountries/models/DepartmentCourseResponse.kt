package com.noob.apps.mvvmcountries.models

data class DepartmentCourseResponse(
    val status: String,
    val message: String,
    val errors: String,
    val data: List<Course>

)
