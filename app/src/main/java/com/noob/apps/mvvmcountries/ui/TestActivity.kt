package com.noob.apps.mvvmcountries.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.ActivityLoginBinding
import com.noob.apps.mvvmcountries.databinding.ActivityTestBinding

class TestActivity:AppCompatActivity() {

    private lateinit var mActivityBinding: ActivityTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_test)



        //initializeRecyclerView()
        // initializeObservers()
    }

}