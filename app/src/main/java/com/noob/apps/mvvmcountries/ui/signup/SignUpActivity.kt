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
        mActivityBinding.txtLogin.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
        }
        mActivityBinding.continueButton.setOnClickListener {
            fullName = mActivityBinding.etFullName.text.toString()
            eMail = mActivityBinding.etEmail.text.toString()
            mobileNumber = mActivityBinding.etMobileNumber.text.toString()
            password = mActivityBinding.etPassword.text.toString()
          //  if (checkValidation()) {
                val intent = Intent(this@SignUpActivity, VerifyOtpActivity::class.java)
                intent.putExtra("MOBILE_NUMBER", mobileNumber)
                startActivity(intent)

         //   }
        }
    }

    private fun checkValidation(): Boolean {
        var isValid = true
        if (fullName.isEmpty()) {
            mActivityBinding.etFullName.error = getString(R.string.requried_field)
            isValid = false
        }
        if (mobileNumber.isEmpty()) {
            mActivityBinding.etMobileNumber.error = getString(R.string.requried_field)
            isValid = false
        }
        if (!MobileNumberValidator.validCellPhone(mobileNumber)) {
            mActivityBinding.etMobileNumber.error = getString(R.string.invalid_mobile_number)
            isValid = false
        }
        if (!PasswordValidation.isValidPassword(password)) {
            mActivityBinding.etPassword.error = getString(R.string.invalid_password)
            isValid = false
        }
        if (!EmailValidation.validMail(eMail)) {
            mActivityBinding.etEmail.error = getString(R.string.invalid_email)
            isValid = false
        }
        return isValid
    }


}


