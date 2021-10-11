package com.noob.apps.mvvmcountries.ui.profile

import android.os.Bundle
import android.text.Editable
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.ActivityProfileBinding
import com.noob.apps.mvvmcountries.models.User
import com.noob.apps.mvvmcountries.ui.base.BaseActivity
import com.noob.apps.mvvmcountries.ui.dialog.ForgetPasswordBottomDialog
import com.noob.apps.mvvmcountries.utils.Constant

class ProfileActivity : BaseActivity() {
    private lateinit var mActivityBinding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_profile)
        val i = intent
        val mUser: User = i.getSerializableExtra(Constant.USER_DATA) as User
        mActivityBinding.etFullName.text =
            Editable.Factory.getInstance().newEditable(mUser.user_name)
        mActivityBinding.etEmail.text = Editable.Factory.getInstance().newEditable(mUser.user_email)
        mActivityBinding.etMobileNumber.text =
            Editable.Factory.getInstance().newEditable(mUser.user_mobile_number)
        mActivityBinding.txtchangePassword.setOnClickListener {
            val bottomSheetFragment = ForgetPasswordBottomDialog()
            bottomSheetFragment.show(
                supportFragmentManager,
                ForgetPasswordBottomDialog.TAG
            )
        }
        mActivityBinding.backImg.setOnClickListener {
            finish()
        }
    }
}