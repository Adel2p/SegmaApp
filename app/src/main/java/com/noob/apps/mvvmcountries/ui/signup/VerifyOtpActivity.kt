package com.noob.apps.mvvmcountries.ui.signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.ActivityVerifyOtpBinding
import com.mukesh.OnOtpCompletionListener




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
mActivityBinding.otpView.onc
        mActivityBinding.otpView.setListener(OnOtpCompletionListener { otp -> // do Stuff
            mActivityBinding.confirmButton.setImageResource(R.drawable.curved_button_blue);
        })
        mActivityBinding.confirmButton.setOnClickListener{
        val otp =mActivityBinding.otpView.text.toString()
            Toast.makeText(this,otp,Toast.LENGTH_LONG).show()
        }
    }
}