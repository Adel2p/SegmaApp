package com.noob.apps.mvvmcountries.ui.signup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.data.DatabaseBuilder
import com.noob.apps.mvvmcountries.data.DatabaseHelperImpl
import com.noob.apps.mvvmcountries.databinding.ActivityVerifyOtpBinding
import com.noob.apps.mvvmcountries.models.OtpModel
import com.noob.apps.mvvmcountries.models.ResendModel
import com.noob.apps.mvvmcountries.ui.base.BaseActivity
import com.noob.apps.mvvmcountries.ui.dialog.ConnectionDialogFragment
import com.noob.apps.mvvmcountries.ui.home.HomeActivity
import com.noob.apps.mvvmcountries.ui.login.LoginActivity
import com.noob.apps.mvvmcountries.ui.welcome.UniversityActivity
import com.noob.apps.mvvmcountries.utils.Constant
import com.noob.apps.mvvmcountries.utils.ViewModelFactory
import com.noob.apps.mvvmcountries.viewmodels.LoginViewModel
import com.noob.apps.mvvmcountries.viewmodels.RegistrationViewModel
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit


class VerifyOtpActivity : BaseActivity() {
    private lateinit var mActivityBinding: ActivityVerifyOtpBinding
    private lateinit var mViewModel: RegistrationViewModel
    private lateinit var loginViewModel: LoginViewModel
    private var mobileNumber = ""
    private var password = ""
    private var userId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_verify_otp)
        mobileNumber = intent.getStringExtra(Constant.MOBILE_NUMBER).toString()
        password = intent.getStringExtra(Constant.USER_PASSWORD).toString()
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
//                startActivity(Intent(this@VerifyOtpActivity, LoginActivity::class.java))
//                finishAffinity()
                initializeObservers()
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

    private fun initializeObservers() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                application,
                DatabaseHelperImpl(DatabaseBuilder.getInstance(applicationContext))
            )
        ).get(LoginViewModel::class.java)

        loginViewModel.fetchCountriesFromServer(mobileNumber, password)
            .observeOnce(this, { user ->
                if (user != null) {
                    lifecycleScope.launch {
                        user.user_uuid?.let { userPreferences.saveUserId(it) }
                    }
                    lifecycleScope.launch {
                        userPreferences.saveUserLogedIn(true)
                    }
                    lifecycleScope.launch {
                        user.refresh_token?.let { userPreferences.saveRefreshToken(it) }
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(TimeUnit.SECONDS.toMillis(1))
                        withContext(Dispatchers.Main) {
                            user.user_on_boarded?.let { checkUserOnBoard(it) }
                        }
                    }
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

    private fun checkUserOnBoard(onboarded: Boolean) {
        if (onboarded) {
            startActivity(Intent(this@VerifyOtpActivity, HomeActivity::class.java))
            finishAffinity()
        } else {
            startActivity(Intent(this@VerifyOtpActivity, UniversityActivity::class.java))
            finishAffinity()
        }

    }
}