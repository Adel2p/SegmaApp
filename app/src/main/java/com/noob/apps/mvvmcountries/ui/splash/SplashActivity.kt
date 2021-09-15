package com.noob.apps.mvvmcountries.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.asLiveData
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.ui.base.BaseActivity
import com.noob.apps.mvvmcountries.ui.home.HomeActivity
import com.noob.apps.mvvmcountries.ui.login.LoginActivity
import com.noob.apps.mvvmcountries.ui.welcome.UniversityActivity
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class SplashActivity : BaseActivity() {
    private var isSaved = false
    private var isloggedin = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash2)
        userPreferences.getUniversityData.asLiveData().observe(this, {
            isSaved = it
        })
        userPreferences.savedLogginedFlow.asLiveData().observe(this, {
            isloggedin = it
        })
        CoroutineScope(Dispatchers.IO).launch {
            delay(TimeUnit.SECONDS.toMillis(3))
            withContext(Dispatchers.Main) {
                if (isloggedin) {
                    if (isSaved) {
                        val intent = Intent(this@SplashActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this@SplashActivity, UniversityActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()

                }


            }
        }
    }
}
