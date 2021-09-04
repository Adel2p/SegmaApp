package com.noob.apps.mvvmcountries.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.ActivityHomeBinding
import com.noob.apps.mvvmcountries.ui.course.CoursesFragment
import com.noob.apps.mvvmcountries.ui.library.LibraryFragment
import com.noob.apps.mvvmcountries.ui.more.MoreFragment

class HomeActivity : AppCompatActivity() {
    private lateinit var mActivityBinding:ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_home)
        val homeFragment=HomeFragment()
        val coursesFragment=CoursesFragment()
        val libraryFragment=LibraryFragment()
        val moreFragment=MoreFragment()
        setCurrentFragment(homeFragment)
        mActivityBinding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home->setCurrentFragment(homeFragment)
                R.id.courses->setCurrentFragment(coursesFragment)
                R.id.library->setCurrentFragment(libraryFragment)
                R.id.more->setCurrentFragment(moreFragment)

            }
            true
        }
    }
    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container,fragment)
            commit()
        }
}