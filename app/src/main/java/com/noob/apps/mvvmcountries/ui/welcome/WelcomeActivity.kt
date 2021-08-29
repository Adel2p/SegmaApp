package com.noob.apps.mvvmcountries.ui.welcome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.CountriesListItemBinding

class WelcomeActivity : AppCompatActivity() {
    private lateinit var mActivityBinding: ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.fragment_welcome)
        mActivityBinding.continueButton.setOnClickListener {

        }
    }
}