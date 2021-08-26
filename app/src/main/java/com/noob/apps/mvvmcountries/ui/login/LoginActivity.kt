package com.noob.apps.mvvmcountries.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.ActivityLoginBinding
import com.noob.apps.mvvmcountries.ui.signup.SignUpActivity
import com.noob.apps.mvvmcountries.ui.visitor.VisitorActivity
import com.noob.apps.mvvmcountries.utils.MobileNumberValidator
import com.noob.apps.mvvmcountries.utils.PasswordValidation

class LoginActivity : AppCompatActivity() {
    private lateinit var mobileNumber: String
    private lateinit var password: String
    private lateinit var mActivityBinding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_login)

        mActivityBinding.txtVisitor.setOnClickListener {
            startActivity(Intent(this@LoginActivity, VisitorActivity::class.java))
        }
        mActivityBinding.txtCreateNewAccount.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
        }
        mActivityBinding.loginButton.setOnClickListener {
            mobileNumber = mActivityBinding.etMobileNumber.text.toString()
            password = mActivityBinding.etPassword.text.toString()
            if (MobileNumberValidator.validCellPhone(mobileNumber)&&mobileNumber.isNotEmpty()) {
                Toast.makeText(this, mobileNumber, Toast.LENGTH_LONG).show()
            } else {
                mActivityBinding.etMobileNumber.error = "Invalid Mobile Number"
            }
            if (PasswordValidation.isValidPassword(password)&&password.isNotEmpty()) {
                Toast.makeText(this, password, Toast.LENGTH_LONG).show()
            } else {
                mActivityBinding.etPassword.error = "Invalid Password"
            }
        }
    }
}