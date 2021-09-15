package com.noob.apps.mvvmcountries.ui.signup

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.provider.FontsContractCompat.Columns.RESULT_CODE
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.data.RoomViewModel
import com.noob.apps.mvvmcountries.databinding.ActivitySignUpBinding
import com.noob.apps.mvvmcountries.models.RegistrationModel
import com.noob.apps.mvvmcountries.models.RegistrationResponse
import com.noob.apps.mvvmcountries.ui.dialog.ConnectionDialogFragment
import com.noob.apps.mvvmcountries.ui.base.BaseActivity
import com.noob.apps.mvvmcountries.utils.Constant
import com.noob.apps.mvvmcountries.utils.EmailValidation
import com.noob.apps.mvvmcountries.utils.MobileNumberValidator
import com.noob.apps.mvvmcountries.viewmodels.RegistrationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class SignUpActivity : BaseActivity() {
    private var LAUNCH_SECOND_ACTIVITY = 1
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
            finish()
        }
        mActivityBinding.continueButton.setOnClickListener {
            fullName = mActivityBinding.etFullName.text.toString()
            eMail = mActivityBinding.etEmail.text.toString()
            mobileNumber = mActivityBinding.etMobileNumber.text.toString()
            password = mActivityBinding.etPassword.text.toString()
            if (checkValidation()) {
                initializeObservers()
            }
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
                "MALE", "ANDROID",
                deviceId,
            )
        ).observe(this, { kt ->
            if (kt != null)
                insertUser(kt)
        })
        mViewModel.mShowResponseError.observeOnce(this, {
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

    private fun insertUser(response: RegistrationResponse) {
        lifecycleScope.launch(Dispatchers.IO) {
            userPreferences.saveUserId(response.data.uuid)
        }

        val intent = Intent(this@SignUpActivity, VerifyOtpActivity::class.java)
        intent.putExtra(Constant.MOBILE_NUMBER, mobileNumber)
        intent.putExtra(Constant.USER_ID, response.data.uuid)
        startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                mActivityBinding.etFullName.isEnabled = false
                mActivityBinding.etEmail.isEnabled = false
                mActivityBinding.etPassword.isEnabled = false
            }
        }
    }


}


