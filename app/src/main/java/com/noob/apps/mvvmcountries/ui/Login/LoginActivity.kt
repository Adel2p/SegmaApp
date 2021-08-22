package com.noob.apps.mvvmcountries.ui.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.ui.CountriesListActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


    }

    override fun onBackPressed() {

        val intent= Intent(this@LoginActivity, CountriesListActivity::class.java)
        startActivity(intent)

    }
}