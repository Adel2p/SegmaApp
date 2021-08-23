package com.noob.apps.mvvmcountries.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.ActivityCountriesListBinding
import com.noob.apps.mvvmcountries.databinding.ActivityLoginBinding
import com.noob.apps.mvvmcountries.ui.CountriesListActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var mActivityBinding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_login)
//       val myButton1 = findViewById<View>(R.id.loginButton)
//       val signin_textview =findViewById<View>(R.id.txtCreateNewAccount)
//       myButton1.setOnClickListener {
//           startActivity(Intent(this@LoginActivity, Visitor::class.java))
//       }
//        signin_textview.setOnClickListener{
//
//            startActivity(Intent(this@LoginActivity, Visitor::class.java))
//       }

    }

    override fun onBackPressed() {

        val intent= Intent(this@LoginActivity, CountriesListActivity::class.java)
        startActivity(intent)

    }
}