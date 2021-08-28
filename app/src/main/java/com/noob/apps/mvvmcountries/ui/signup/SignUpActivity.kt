package com.noob.apps.mvvmcountries.ui.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.ActivitySignUpBinding
import com.noob.apps.mvvmcountries.ui.login.LoginActivity
import com.noob.apps.mvvmcountries.utils.EmailValidation
import com.noob.apps.mvvmcountries.utils.MobileNumberValidator
import com.noob.apps.mvvmcountries.utils.PasswordValidation

class SignUpActivity : AppCompatActivity() {
    private lateinit var fullName: String
    private lateinit var eMail: String
    private lateinit var mobileNumber: String
    private lateinit var password: String
    private lateinit var mActivityBinding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        mActivityBinding.txtlogin.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
        }
        mActivityBinding.continueBotton.setOnClickListener {
            fullName = mActivityBinding.etFullName.text.toString()
            eMail = mActivityBinding.etemail.text.toString()
            mobileNumber = mActivityBinding.etMobileNumber.text.toString()
            password = mActivityBinding.etPassword.text.toString()
            if (checkValidation())
                startActivity(Intent(this@SignUpActivity, VerifyOtpActivity::class.java))

        }
    }

    private fun checkValidation(): Boolean {
        var isValid = true
        if (fullName.isEmpty()) {
            mActivityBinding.etFullName.error = getString(R.string.requried_field)
            isValid = false
        }
        if (mobileNumber.isEmpty()) {
            mActivityBinding.etMobileNumber.error = "This Term Is Requried"
            isValid = false
        }
        if (!MobileNumberValidator.validCellPhone(mobileNumber)) {
            mActivityBinding.etMobileNumber.error = "Invalid Mobile Number"
            isValid = false
        }
        if (!PasswordValidation.isValidPassword(password)) {
            mActivityBinding.etPassword.error = "Invalid  Password"
            isValid = false
        }
        if (!EmailValidation.validMail(eMail)) {
            mActivityBinding.etemail.error = "Invalid Email"
            isValid = false
        }
        return isValid
    }


}


