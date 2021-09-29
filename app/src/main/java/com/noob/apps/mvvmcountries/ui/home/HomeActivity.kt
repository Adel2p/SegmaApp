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
import com.noob.apps.mvvmcountries.ui.fcm.MyFirebaseMessagingService


class HomeActivity : BaseActivity() {
    private lateinit var mActivityBinding: ActivityHomeBinding
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
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment)
            commit()
        }
}