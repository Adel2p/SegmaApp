package com.noob.apps.mvvmcountries.ui.signup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.ActivityVerifyOtpBinding
import com.noob.apps.mvvmcountries.models.OtpModel
import com.noob.apps.mvvmcountries.models.ResendModel
import com.noob.apps.mvvmcountries.ui.base.BaseActivity
import com.noob.apps.mvvmcountries.ui.dialog.ConnectionDialogFragment
import com.noob.apps.mvvmcountries.ui.login.LoginActivity
import com.noob.apps.mvvmcountries.utils.Constant
import com.noob.apps.mvvmcountries.viewmodels.RegistrationViewModel


class VerifyOtpActivity : BaseActivity() {
    private lateinit var mActivityBinding: ActivityVerifyOtpBinding
    private lateinit var mViewModel: RegistrationViewModel
    private var mobileNumber = ""
    private var userId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_verify_otp)
        mobileNumber = intent.getStringExtra(Constant.MOBILE_NUMBER).toString()
        userId = intent.getStringExtra(Constant.USER_ID).toString()
        mViewModel = ViewModelProvider(this).get(RegistrationViewModel::class.java)
        mActivityBinding.txtMobileNumber.text = mobileNumber
        mActivityBinding.txtChangeNumber.setOnClickListener {
            val returnIntent = Intent()
            setResult(RESULT_OK, returnIntent)
            finish()
        }
        mActivityBinding.otpView.setOtpCompletionListener {
            mActivityBinding.confirmButton.setBackgroundResource(R.drawable.curved_button_blue)
        }
        mActivityBinding.confirmButton.setOnClickListener {
            val otp = mActivityBinding.otpView.text.toString()
            initializeObservers(otp)

        }
        mActivityBinding.resend.setOnClickListener {
            initializeResendObservers()

        }
    }

    private fun initializeObservers(otp: String) {
        mViewModel.verifyOtp(
            OtpModel(
                userId,
                otp
            )
        ).observeOnce(this, { kt ->
            if (kt != null) {
                startActivity(Intent(this@VerifyOtpActivity, LoginActivity::class.java))
                finishAffinity()
            }
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

    private fun initializeResendObservers() {
        mViewModel.resendOtp(
            ResendModel(
                userId
            )
        ).observeOnce(this, { kt ->
            if (kt != null) {
                Toast.makeText(this, getString(R.string.send_successfully), Toast.LENGTH_LONG)
                    .show()
            }
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

    override fun onBackPressed() {
        val returnIntent = Intent()
        setResult(RESULT_CANCELED, returnIntent)
        finish()
        super.onBackPressed()

    }
}