package com.noob.apps.mvvmcountries.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.data.DatabaseBuilder
import com.noob.apps.mvvmcountries.data.DatabaseHelperImpl
import com.noob.apps.mvvmcountries.models.RefreshTokenModel
import com.noob.apps.mvvmcountries.ui.base.BaseActivity
import com.noob.apps.mvvmcountries.ui.dialog.ConnectionDialogFragment
import com.noob.apps.mvvmcountries.ui.home.HomeActivity
import com.noob.apps.mvvmcountries.ui.login.LoginActivity
import com.noob.apps.mvvmcountries.ui.welcome.UniversityActivity
import com.noob.apps.mvvmcountries.utils.Constant
import com.noob.apps.mvvmcountries.utils.ViewModelFactory
import com.noob.apps.mvvmcountries.viewmodels.SplashViewModel
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class SplashActivity : BaseActivity() {
    private var isSaved = false
    private var isloggedin = false
    private lateinit var splashViewModel: SplashViewModel
    private var refreshToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash2)
        splashViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                application,
                DatabaseHelperImpl(DatabaseBuilder.getInstance(applicationContext))
            )
        ).get(SplashViewModel::class.java)

        userPreferences.savedLogginedFlow.asLiveData().observeOnce(this, {
            isloggedin = it
            if (isloggedin)
                readISSaved()
            else
                openLogin()

        })


    }

    private fun readToken() {
        userPreferences.getRefreshToken.asLiveData().observeOnce(this, {
            refreshToken = it
            if (refreshToken.isNotEmpty())
                initTokenObservers()
            else
                navigate()


        })
    }

    private fun readISSaved() {
        userPreferences.getUniversityData.asLiveData().observeOnce(this, {
            isSaved = it
            readToken()
        })
    }

    private fun initTokenObservers() {
        splashViewModel.updateToken(RefreshTokenModel(Constant.REFRESH_TOKEN, refreshToken))
        splashViewModel.updateTokenResponse.observeOnce(this, { kt ->
            if (kt != null) {
                navigate()
            }
        })
        splashViewModel.mShowResponseError.observeOnce(this, {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finishAffinity()
        })
        splashViewModel.mShowProgressBar.observe(this, { bt ->
            if (bt) {
                showLoader()
            } else {
                hideLoader()
            }

        })
        splashViewModel.mShowNetworkError.observeOnce(this, {
            if (it != null) {
                ConnectionDialogFragment.newInstance(Constant.RETRY_LOGIN)
                    .show(supportFragmentManager, ConnectionDialogFragment.TAG)
            }

        })
    }

    private fun navigate() {
        CoroutineScope(Dispatchers.IO).launch {
            delay(TimeUnit.SECONDS.toMillis(0))
            withContext(Dispatchers.Main) {
                if (isloggedin) {
                    if (isSaved) {
                        openHome()
                    } else {
                        openUniversity()
                    }
                } else {
                    openLogin()

                }


            }
        }
    }

    private fun openHome() {
        val intent = Intent(this@SplashActivity, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun openUniversity() {
        val intent =
            Intent(this@SplashActivity, UniversityActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun openLogin() {
        val intent = Intent(this@SplashActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
