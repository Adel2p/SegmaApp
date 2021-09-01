package com.noob.apps.mvvmcountries.ui.signup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.ActivityVerifyOtpBinding
import com.noob.apps.mvvmcountries.ui.welcome.WelcomeActivity


class VerifyOtpActivity : AppCompatActivity() {
    private lateinit var mActivityBinding: ActivityVerifyOtpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_verify_otp)
        val mobileNumber = intent.getStringExtra("MOBILE_NUMBER")
        mActivityBinding.txtMobileNumber.text = mobileNumber.toString()
        mActivityBinding.txtChangeNumber.setOnClickListener {
            finish()
        }
        mActivityBinding.otpView.setOtpCompletionListener {
            mActivityBinding.confirmButton.setBackgroundResource(R.drawable.curved_button_blue)
        }
        mActivityBinding.confirmButton.setOnClickListener {
            val otp = mActivityBinding.otpView.text.toString()
            // Toast.makeText(this, otp, Toast.LENGTH_LONG).show()
            startActivity(Intent(this@VerifyOtpActivity, WelcomeActivity::class.java))
            finish()
        }
    }
}