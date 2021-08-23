package com.noob.apps.mvvmcountries.ui.Splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.ui.login.LoginActivity
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash2)
        CoroutineScope(Dispatchers.IO).launch {
            delay(TimeUnit.SECONDS.toMillis(3))
            withContext(Dispatchers.Main) {
                val intent = Intent(this@SplashActivity,LoginActivity::class.java)

                startActivity(intent)
                finish()
            }
        }

    }
    }
