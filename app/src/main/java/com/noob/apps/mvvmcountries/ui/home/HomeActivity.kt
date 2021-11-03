package com.noob.apps.mvvmcountries.ui.home

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.ActivityHomeBinding
import com.noob.apps.mvvmcountries.ui.base.BaseActivity
import com.noob.apps.mvvmcountries.ui.course.CoursesFragment
import com.noob.apps.mvvmcountries.ui.library.LibraryFragment
import com.noob.apps.mvvmcountries.ui.more.MoreFragment
import kotlinx.coroutines.launch
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.mediarouter.media.MediaRouter
import com.noob.apps.mvvmcountries.ui.dialog.BlockUserDialog
import com.noob.apps.mvvmcountries.ui.fcm.MyFirebaseMessagingService


class HomeActivity : BaseActivity() {
    private val TAG = "HomeActivity"
    private var mMediaRouter: MediaRouter? = null
    private val DISCOVERY_FRAGMENT_TAG = "DiscoveryFragment"
    private lateinit var mActivityBinding: ActivityHomeBinding
    private val mMediaRouterCB: MediaRouter.Callback = object : MediaRouter.Callback() {
        override fun onRouteAdded(router: MediaRouter, route: MediaRouter.RouteInfo) {
            Log.d(
                TAG,
                "onRouteAdded: route=$route"
            )

        }

        override fun onRouteChanged(router: MediaRouter, route: MediaRouter.RouteInfo) {
            Log.d(
                TAG,
                "onRouteChanged: route=$route"
            )

        }


        override fun onRouteRemoved(router: MediaRouter, route: MediaRouter.RouteInfo) {
            Log.d(
                TAG,
                "onRouteRemoved: route=$route"
            )

        }

        override fun onRouteSelected(router: MediaRouter, route: MediaRouter.RouteInfo) {
            Log.d(
                TAG,
                "onRouteSelected: route=$route"
            )
            if (route.connectionState == 2)
                showBlockDialog("You Cannot run App on Screen Mirroring")
        }

        override fun onRouteUnselected(router: MediaRouter, route: MediaRouter.RouteInfo) {
            Log.d(
                TAG,
                "onRouteUnselected: route=$route"
            )

            hideBlockDialog()
        }

        override fun onRouteVolumeChanged(router: MediaRouter, route: MediaRouter.RouteInfo) {
        }

        override fun onRoutePresentationDisplayChanged(
            router: MediaRouter,
            route: MediaRouter.RouteInfo
        ) {
            Log.d(
                TAG,
                "onRoutePresentationDisplayChanged: route=$route"
            )
            if (route.connectionState == 2)
                showBlockDialog("You Cannot run App on Screen Mirroring")

        }

        override fun onProviderAdded(router: MediaRouter, provider: MediaRouter.ProviderInfo) {
        }

        override fun onProviderRemoved(router: MediaRouter, provider: MediaRouter.ProviderInfo) {
        }

        override fun onProviderChanged(router: MediaRouter, provider: MediaRouter.ProviderInfo) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_home)
        lifecycleScope.launch {
            userPreferences.saveUniversityData(true)
        }
        userPreferences.getFirebaseEnabled.asLiveData().observeOnce(this, {
            if (!it) {
                val myService = Intent(this@HomeActivity, MyFirebaseMessagingService::class.java)
                stopService(myService)
            }

        })
        val homeFragment = HomeFragment()
        val coursesFragment = CoursesFragment()
        val libraryFragment = LibraryFragment()
        val moreFragment = MoreFragment()
        setCurrentFragment(homeFragment)
        mActivityBinding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> setCurrentFragment(homeFragment)
                R.id.courses -> setCurrentFragment(coursesFragment)
                R.id.library -> setCurrentFragment(libraryFragment)
                R.id.more -> setCurrentFragment(moreFragment)

            }
            true
        }
//        if (Settings.Secure.getInt(contentResolver, Settings.Secure.ADB_ENABLED, 0) == 1) {
//            return BlockUserDialog.newInstance("Please turn off usb debugging\n")
//                .show(supportFragmentManager, BlockUserDialog.TAG)
//        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment)
            commit()
        }

    override fun onResume() {
        super.onResume()
        mMediaRouter = MediaRouter.getInstance(this)
        val fm = supportFragmentManager
        val fragment: DiscoveryFragment?
        fragment = DiscoveryFragment()
        fragment.setCallback(mMediaRouterCB)
        fm.beginTransaction().add(fragment, DISCOVERY_FRAGMENT_TAG).commit()
    }
}