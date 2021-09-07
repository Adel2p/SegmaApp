package com.noob.apps.mvvmcountries.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.AboutSegmaDialogBinding
import com.noob.apps.mvvmcountries.databinding.FragmentMoreBinding

class AboutSegmaDialog : BottomSheetDialogFragment() {
    private lateinit var mActivityBinding: AboutSegmaDialogBinding

    companion object {

        const val TAG = "AboutSegmaDialog"

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mActivityBinding =
            DataBindingUtil.inflate(inflater, R.layout.about_segma_dialog, container, false)
        // Inflate the layout for this fragment
        return mActivityBinding.root    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mActivityBinding.closeImg.setOnClickListener{
            dismiss()
        }



    }
}