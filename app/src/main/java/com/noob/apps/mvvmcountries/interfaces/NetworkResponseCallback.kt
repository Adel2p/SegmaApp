package com.noob.apps.mvvmcountries.interfaces

interface NetworkResponseCallback {
    fun onNetworkSuccess()
    fun onResponseError(message: String)
    fun onNetworkFailure(th: Throwable)
}