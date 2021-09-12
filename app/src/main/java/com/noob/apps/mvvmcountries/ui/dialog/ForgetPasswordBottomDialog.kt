package com.noob.apps.mvvmcountries.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.ForgetPasswordBottomDialogBinding

class ForgetPasswordBottomDialog : BottomSheetDialogFragment() {
    private lateinit var mActivityBinding: ForgetPasswordBottomDialogBinding
    private lateinit var oldPassword: String
    private lateinit var newPassword: String
    private lateinit var confirmPassword: String



    companion object {

        const val TAG = "ForgetPasswordBottomDialog"

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mActivityBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.forget_password_bottom_dialog,
                container,
                false
            )
        // Inflate the layout for this fragment
        return mActivityBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mActivityBinding.txtCancel.setOnClickListener {
            dismiss()
        }
        mActivityBinding.continueButton.setOnClickListener {
             oldPassword = mActivityBinding.etOldPassword.text.toString()
             newPassword = mActivityBinding.etNewPassword.text.toString()
             confirmPassword = mActivityBinding.etConfirmNewPassword.text.toString()

            if (checkValidation(oldPassword, newPassword, confirmPassword))
                Toast.makeText(requireActivity(),"Well Done",Toast.LENGTH_LONG).show()

        }
    }

    private fun checkValidation(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Boolean {

        var isValid = true

        if (oldPassword.isEmpty()) {
            mActivityBinding.etOldPassword.error = "Required Field"
            isValid = false
        }
        if (newPassword.isEmpty()) {
            mActivityBinding.etNewPassword.error = "Required Field"
            isValid = false
        }

        if (confirmPassword.isEmpty()) {
            mActivityBinding.etConfirmNewPassword.error = "Required Field"
            isValid = false
        }

        if (newPassword != confirmPassword) {
            mActivityBinding.etConfirmNewPassword.error = "Not Confirmed"
            isValid = false
        }
        return isValid
    }


}
