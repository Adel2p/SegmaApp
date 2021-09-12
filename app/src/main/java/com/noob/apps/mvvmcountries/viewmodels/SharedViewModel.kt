package com.noob.apps.mvvmcountries.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {

    val operation = MutableLiveData<String>()

    fun sendOperation(text: String) {
        operation.value = text
    }
}