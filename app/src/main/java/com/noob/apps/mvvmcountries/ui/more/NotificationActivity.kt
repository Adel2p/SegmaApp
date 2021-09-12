package com.noob.apps.mvvmcountries.ui.more

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.adapters.NotificationAdapter
import com.noob.apps.mvvmcountries.databinding.ActivityNotificationBinding
import com.noob.apps.mvvmcountries.models.Notification

class NotificationActivity : AppCompatActivity() {
    private lateinit var mAdapter: NotificationAdapter
    private val listOfNotifications: MutableList<Notification> = mutableListOf()
    private lateinit var mActivityBinding: ActivityNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_notification)
        val notification1 = Notification("computer science", "Content")
        val notification2 = Notification("computer science", "content")
        val notification3 = Notification("computer science", "content")
        val notification4 = Notification("computer science", "content")
        val notification5 = Notification("computer science", "content")
        val notification6 = Notification("computer science", "content")
        val notification7 = Notification("computer science", "content")
        val notification8 = Notification("computer science", "content")


        listOfNotifications.add(notification1)
        listOfNotifications.add(notification2)
        listOfNotifications.add(notification3)
        listOfNotifications.add(notification4)
        listOfNotifications.add(notification5)
        listOfNotifications.add(notification6)
        listOfNotifications.add(notification7)
        listOfNotifications.add(notification8)

        initializeRecyclerView()
        mAdapter.setData(listOfNotifications)
        mActivityBinding.backImg.setOnClickListener{
            finish()
        }

    }
    private fun initializeRecyclerView() {
        mAdapter = NotificationAdapter()
        mActivityBinding.notificationRec.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(this@NotificationActivity, 1)

            adapter = mAdapter
        }
    }
}