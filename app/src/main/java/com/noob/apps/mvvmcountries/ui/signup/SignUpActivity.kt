package com.noob.apps.mvvmcountries.ui.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.ActivitySignUpBinding
import com.noob.apps.mvvmcountries.ui.login.LoginActivity
import com.noob.apps.mvvmcountries.ui.visitor.VisitorActivity

class SignUpActivity : AppCompatActivity() {
    private lateinit var nActivityBinding:ActivitySignUpBinding
            override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
                nActivityBinding =
                    DataBindingUtil.setContentView(this, R.layout.activity_sign_up)

                nActivityBinding.txtlogin.setOnClickListener {
                    finish()
                }
    }
}