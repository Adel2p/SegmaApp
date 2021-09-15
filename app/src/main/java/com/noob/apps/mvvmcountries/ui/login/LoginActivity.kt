package com.noob.apps.mvvmcountries.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.data.DatabaseBuilder
import com.noob.apps.mvvmcountries.data.DatabaseHelperImpl
import com.noob.apps.mvvmcountries.data.RoomViewModel
import com.noob.apps.mvvmcountries.databinding.ActivityLoginBinding
import com.noob.apps.mvvmcountries.ui.base.BaseActivity
import com.noob.apps.mvvmcountries.ui.dialog.ConnectionDialogFragment
import com.noob.apps.mvvmcountries.ui.forgetpassword.ForgetPasswordActivity
import com.noob.apps.mvvmcountries.ui.home.HomeActivity
import com.noob.apps.mvvmcountries.ui.signup.SignUpActivity
import com.noob.apps.mvvmcountries.ui.visitor.VisitorActivity
import com.noob.apps.mvvmcountries.ui.welcome.UniversityActivity
import com.noob.apps.mvvmcountries.utils.Constant
import com.noob.apps.mvvmcountries.utils.MobileNumberValidator
import com.noob.apps.mvvmcountries.utils.ViewModelFactory
import com.noob.apps.mvvmcountries.viewmodels.LoginViewModel
import com.noob.apps.mvvmcountries.viewmodels.SharedViewModel
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class LoginActivity : BaseActivity() {
    private lateinit var mobileNumber: String
    private lateinit var password: String
    private lateinit var mActivityBinding: ActivityLoginBinding
    private lateinit var mViewModel: LoginViewModel
    private lateinit var roomViewModel: RoomViewModel
    private lateinit var sharedViewModel: SharedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_login)


        mActivityBinding.txtVisitor.setOnClickListener {
            startActivity(Intent(this@LoginActivity, VisitorActivity::class.java))
        }
        mActivityBinding.txtForgetPassword.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgetPasswordActivity::class.java))
        }
        mActivityBinding.txtCreateNewAccount.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
        }

        mViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                application,
                DatabaseHelperImpl(DatabaseBuilder.getInstance(applicationContext))
            )
        ).get(LoginViewModel::class.java)

        roomViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                application,
                DatabaseHelperImpl(DatabaseBuilder.getInstance(applicationContext))
            )
        ).get(RoomViewModel::class.java)
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        sharedViewModel.operation.observe(this, Observer {
            if (it == Constant.RETRY_LOGIN)
                initializeObservers()
        })
        mActivityBinding.loginButton.setOnClickListener {
            hideKeyboard()
            mobileNumber = mActivityBinding.etMobileNumber.text.toString()
            password = mActivityBinding.etPassword.text.toString()
            if (checkValidation()) {
                initializeObservers()
            }

        }
    }

    private fun checkValidation(): Boolean {
        var isValid = true
        if (!MobileNumberValidator.validCellPhone(mobileNumber)) {
            mActivityBinding.etMobileNumber.error = getString(R.string.invalid_mobile_number)
            isValid = false
        }
        if (password.isEmpty()) {
            mActivityBinding.etPassword.error = getString(R.string.invalid_password)
            isValid = false
        }
        return isValid
    }

    private fun initializeObservers() {
        mViewModel.fetchCountriesFromServer(mobileNumber, password).observe(this, Observer { user ->
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
        mViewModel.mShowResponseError.observe(this, Observer {
            AlertDialog.Builder(this).setMessage(it).show()
        })
        mViewModel.mShowProgressBar.observe(this, Observer { bt ->
            if (bt) {
                showLoader()
            } else {
                hideLoader()
            }
        })
        mViewModel.mShowNetworkError.observe(this, Observer {
            ConnectionDialogFragment.newInstance(Constant.RETRY_LOGIN)
                .show(supportFragmentManager, ConnectionDialogFragment.TAG)

        })
    }

    private fun checkUserOnBoard(onboarded: Boolean) {
        if (onboarded) {
            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this@LoginActivity, UniversityActivity::class.java))
            finish()
        }

    }

}