package com.noob.apps.mvvmcountries.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RestClient private constructor() {
    companion object {
        private const val BASE_URL = "http://ec2-18-188-150-5.us-east-2.compute.amazonaws.com:8080/api/v1/"
        private lateinit var mApiServices: ApiServices
        private var mInstance: RestClient? = null
        fun getInstance(): RestClient {
            if (mInstance == null) {
                synchronized(this) {
                    mInstance = RestClient()
                }
            }
            return mInstance!!
        }
    }

    init {
        val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val okHttpClient = OkHttpClient().newBuilder().connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(logger)
            .build()
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        mApiServices = retrofit.create(ApiServices::class.java)
    }

    fun getApiService() = mApiServices
}