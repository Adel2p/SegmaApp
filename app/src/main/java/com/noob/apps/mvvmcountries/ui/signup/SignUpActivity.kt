package com.noob.apps.mvvmcountries.ui.signup

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.ActivitySignUpBinding
import com.noob.apps.mvvmcountries.models.RegistrationModel
import com.noob.apps.mvvmcountries.ui.dialog.ConnectionDialogFragment
import com.noob.apps.mvvmcountries.ui.login.LoginActivity
import android.provider.Settings
import com.noob.apps.mvvmcountries.ui.base.BaseActivity
import com.noob.apps.mvvmcountries.utils.Constant
import com.noob.apps.mvvmcountries.utils.EmailValidation
import com.noob.apps.mvvmcountries.utils.MobileNumberValidator
import com.noob.apps.mvvmcountries.viewmodels.RegistrationViewModel

class SignUpActivity : BaseActivity() {
    private lateinit var fullName: String
    private lateinit var eMail: String
    private lateinit var mobileNumber: String
    private lateinit var password: String
    private lateinit var mActivityBinding: ActivitySignUpBinding
    private lateinit var mViewModel: RegistrationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        mViewModel = ViewModelProvider(this).get(RegistrationViewModel::class.java)
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
            //  intent.putExtra("FULL_NAME",fullName)
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
        if (password.isEmpty()) {
            mActivityBinding.etPassword.error = getString(R.string.invalid_password)
            isValid = false
        }
        if (!EmailValidation.validMail(eMail)) {
            mActivityBinding.etEmail.error = getString(R.string.invalid_email)
            isValid = false
        }
        return isValid
    }

    private fun initializeObservers() {
        mViewModel.register(
            RegistrationModel(
                fullName,
                eMail,
                mobileNumber,
                password,
                "MALE", "",
                id
            )
        ).observe(this, { kt ->
            if (kt.isSuccess == true)
                Toast.makeText(this, kt.user_name, Toast.LENGTH_LONG).show()
            else
                Toast.makeText(this, kt.error_description, Toast.LENGTH_LONG).show()

        })
        mViewModel.mShowResponseError.observe(this, {
            AlertDialog.Builder(this).setMessage(it).show()
        })
        mViewModel.mShowProgressBar.observe(this, { bt ->
            if (bt) {
                showLoader()
            } else {
                hideLoader()
            }
        })
        mViewModel.mShowNetworkError.observe(this, {
            ConnectionDialogFragment.newInstance(Constant.RETRY_LOGIN)
                .show(supportFragmentManager, ConnectionDialogFragment.TAG)

        })
    }


}


