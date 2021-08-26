package com.noob.apps.mvvmcountries.ui.visitor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.ActivityVisitorBinding
import com.noob.apps.mvvmcountries.ui.login.LoginActivity
import com.noob.apps.mvvmcountries.utils.MobileNumberValidator
import com.noob.apps.mvvmcountries.utils.PasswordValidation

class VisitorActivity : AppCompatActivity() {
    private lateinit var fullName: String
    private lateinit var mobileNumber: String
    private lateinit var mActivityBinding: ActivityVisitorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_visitor)
        mActivityBinding.txtlogin.setOnClickListener {
            startActivity(Intent(this@VisitorActivity, LoginActivity::class.java))
        }
        mActivityBinding.VisitorButton.setOnClickListener {
            fullName = mActivityBinding.etFullName.text.toString()
            mobileNumber = mActivityBinding.etMobileNumber.text.toString()
            if (fullName.isNotEmpty()) {
                Toast.makeText(this, fullName, Toast.LENGTH_LONG).show()
            } else {
                mActivityBinding.etFullName.error = "This Term Is Requried"
            }
            if (MobileNumberValidator.validCellPhone(mobileNumber)) {
                Toast.makeText(this, mobileNumber, Toast.LENGTH_LONG).show()
            } else {
                mActivityBinding.etMobileNumber.error = "Invalid Mobile Number"
            }
        }
    }
}