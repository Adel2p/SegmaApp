package com.noob.apps.mvvmcountries.ui.more

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.adapters.NotificationAdapter
import com.noob.apps.mvvmcountries.databinding.ActivityNotificationBinding
import com.noob.apps.mvvmcountries.models.Notification
import com.noob.apps.mvvmcountries.ui.base.BaseActivity
import com.noob.apps.mvvmcountries.ui.dialog.ConnectionDialogFragment
import com.noob.apps.mvvmcountries.utils.Constant
import com.noob.apps.mvvmcountries.viewmodels.NotificationViewModel

class NotificationActivity : BaseActivity() {
    private lateinit var mAdapter: NotificationAdapter
    private val listOfNotifications: MutableList<Notification> = mutableListOf()
    private lateinit var mActivityBinding: ActivityNotificationBinding
    private lateinit var mViewModel: NotificationViewModel
    private var token = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_notification)
        mViewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)
        userPreferences.getUserToken.asLiveData().observeOnce(this, {
            token = it
            initializeRecyclerView()
            initializeObservers()
        })

        mActivityBinding.backImg.setOnClickListener {
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

    private fun initializeObservers() {
        mViewModel.getNotifications(
            token
        ).observe(this, { kt ->
            if (kt != null) {
                listOfNotifications.clear()
                listOfNotifications.addAll(kt.data.toMutableList())
                mAdapter.setData(listOfNotifications)
            }

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

}