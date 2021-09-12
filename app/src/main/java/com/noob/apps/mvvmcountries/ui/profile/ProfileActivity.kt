package com.noob.apps.mvvmcountries.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.ActivityProfileBinding
import com.noob.apps.mvvmcountries.ui.dialog.ForgetPasswordBottomDialog
import com.noob.apps.mvvmcountries.ui.dialog.LanguageBottomDialog
import com.noob.apps.mvvmcountries.ui.forgetpassword.ForgetPasswordActivity
import com.noob.apps.mvvmcountries.ui.login.LoginActivity

class ProfileActivity : AppCompatActivity() {
    private lateinit var mActivityBinding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_profile)
        mActivityBinding.txtchangePassword.setOnClickListener {
            val bottomSheetFragment = ForgetPasswordBottomDialog()
            // activity?.let { it1 ->
            bottomSheetFragment.show(
                supportFragmentManager,
                ForgetPasswordBottomDialog.TAG
            )
            // }
        }
        mActivityBinding.backImg.setOnClickListener {
            finish()
        }
    }
}