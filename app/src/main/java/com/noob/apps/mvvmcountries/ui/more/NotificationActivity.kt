package com.noob.apps.mvvmcountries.ui.more

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.ActivityFavouriteLectureBinding
import com.noob.apps.mvvmcountries.databinding.ActivityNotificationBinding

class NotificationActivity : AppCompatActivity() {
    private lateinit var mActivityBinding: ActivityNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
    }
}