package com.noob.apps.mvvmcountries.ui.forgetpassword

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.ActivityForgetPasswordBinding
import com.noob.apps.mvvmcountries.utils.EmailValidation


class ForgetPasswordActivity : AppCompatActivity() {
    private lateinit var mActivityBinding: ActivityForgetPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_forget_password)
        mActivityBinding.continueButton.setOnClickListener {
            if (mActivityBinding.mobileNumberEt.text.toString().isNotEmpty() &&
                EmailValidation.validMail(mActivityBinding.mobileNumberEt.text.toString())
            )
                startActivity(
                    Intent(
                        this@ForgetPasswordActivity,
                        ForgetPasswordResetActivity::class.java
                    )
                )
        }
    }
}