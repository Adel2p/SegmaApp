package com.noob.apps.mvvmcountries.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.ForgetPasswordBottomDialogBinding

class ForgetPasswordBottomDialog: BottomSheetDialogFragment() {
    private lateinit var mActivityBinding: ForgetPasswordBottomDialogBinding

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
    }
}