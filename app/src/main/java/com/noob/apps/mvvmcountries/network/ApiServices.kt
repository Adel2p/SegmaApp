package com.noob.apps.mvvmcountries.network

import com.noob.apps.mvvmcountries.models.Country
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiServices {

    @GET("all")
    fun getCountries() : Call<List<Country>>


}