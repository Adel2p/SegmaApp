package com.noob.apps.mvvmcountries.ui.welcome

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.ActivityLoginBinding

class WelcomeActivity : AppCompatActivity() {
    private lateinit var mActivityBinding:ActivityWelcomeBinding
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         mActivityBinding =
             DataBindingUtil.setContentView(this, R.layout.activity_welcome)
         mActivityBinding.continueButton.setOnClickListener {

         }
     }
}