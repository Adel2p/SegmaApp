package com.noob.apps.mvvmcountries.ui.more

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.ui.dialog.LanguageBottomDialog

class FavLecActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fav_lec)
        LanguageBottomDialog().apply {
            activity?.let { it1 -> show(it1.supportFragmentManager, LanguageBottomDialog.TAG) }
        }

    }
}