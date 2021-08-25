package com.noob.apps.mvvmcountries.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.ActivityLoginBinding
import com.noob.apps.mvvmcountries.ui.CountriesListActivity
import com.noob.apps.mvvmcountries.ui.visitor.Visitor

class LoginActivity : AppCompatActivity() {
    private lateinit var mActivityBinding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_login)
        mActivityBinding.txtVisitor.setOnClickListener {
            startActivity(Intent(this@LoginActivity, Visitor::class.java))
        }


    }

    override fun onBackPressed() {

        val intent = Intent(this@LoginActivity, CountriesListActivity::class.java)
        startActivity(intent)

    }
}