package com.noob.apps.mvvmcountries.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.ActivityLoginBinding
import com.noob.apps.mvvmcountries.ui.forgetpassword.ForgetPasswordActivity
import com.noob.apps.mvvmcountries.ui.home.HomeActivity
import com.noob.apps.mvvmcountries.ui.signup.SignUpActivity
import com.noob.apps.mvvmcountries.ui.visitor.VisitorActivity
import com.noob.apps.mvvmcountries.ui.welcome.UniversityActivity
import com.noob.apps.mvvmcountries.utils.MobileNumberValidator
import com.noob.apps.mvvmcountries.utils.UserPreferences
import com.noob.apps.mvvmcountries.viewmodels.CountryListViewModel
import com.noob.apps.mvvmcountries.viewmodels.LoginViewModel
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var userPreferences: UserPreferences
    private var isSaved = false
    private lateinit var mobileNumber: String
    private lateinit var password: String
    private lateinit var mActivityBinding: ActivityLoginBinding
    private lateinit var mViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_login)
        userPreferences = UserPreferences(this)

        mActivityBinding.txtVisitor.setOnClickListener {
            startActivity(Intent(this@LoginActivity, VisitorActivity::class.java))
        }
        mActivityBinding.txtForgetPassword.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgetPasswordActivity::class.java))
        }
        mActivityBinding.txtCreateNewAccount.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
        }
        mViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        mActivityBinding.loginButton.setOnClickListener {
            mobileNumber = mActivityBinding.etMobileNumber.text.toString()
            password = mActivityBinding.etPassword.text.toString()
            mViewModel.fetchCountriesFromServer()

//            if (checkValidation()) {
//                ////////save user loged in
//                lifecycleScope.launch {
//                    userPreferences.saveUserLogedIn(true)
//                }
//                userPreferences.getUniversityData.asLiveData().observe(this, {
//                    isSaved = it
//                })
//                if (isSaved) {
//                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
//                    finish()
//                }
//                else{
//                    startActivity(Intent(this@LoginActivity, UniversityActivity::class.java))
//                    finish()
//                }
//            }

        }
    }

    private fun checkValidation(): Boolean {
        var isValid = true
        if (!MobileNumberValidator.validCellPhone(mobileNumber)) {
            mActivityBinding.etMobileNumber.error = "Invalid Mobile Number"
            isValid = false
        }
        if (password.isEmpty()) {
            mActivityBinding.etPassword.error = "Invalid Password"
            isValid = false
        }
        return isValid
    }

}