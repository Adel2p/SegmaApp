package com.noob.apps.mvvmcountries.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.asLiveData
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.ui.forgetpassword.ForgetPasswordActivity
import com.noob.apps.mvvmcountries.ui.login.LoginActivity
import com.noob.apps.mvvmcountries.ui.main.MainActivity
import com.noob.apps.mvvmcountries.ui.welcome.UniversityActivity
import com.noob.apps.mvvmcountries.utils.UserPreferences
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {
    private lateinit var userPreferences: UserPreferences
    private var isSaved = false
    private var isloggedin=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash2)
        userPreferences = UserPreferences(this)
        userPreferences.getUniversityData.asLiveData().observe(this, {
            isSaved = it
        })

        userPreferences.savedLogginedFlow.asLiveData().observe(this, {
            isloggedin = it
        })
        CoroutineScope(Dispatchers.IO).launch {
            delay(TimeUnit.SECONDS.toMillis(3))
            withContext(Dispatchers.Main) {
                if(isloggedin) {
                    if (isSaved) {
                        val intent = Intent(this@SplashActivity, MainActivity::class.java)

                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this@SplashActivity, UniversityActivity::class.java)

                        startActivity(intent)
                        finish()
                    }
                }
                else{
                    val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()

                }


            }
        }

    }
}
