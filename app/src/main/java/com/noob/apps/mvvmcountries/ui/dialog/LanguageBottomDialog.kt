package com.noob.apps.mvvmcountries.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.noob.apps.mvvmcountries.R

class LanguageBottomDialog : BottomSheetDialogFragment() {


    companion object {
        const val TAG = "LanguageBottomDialog"

        fun newInstance(
        ): LanguageBottomDialog {
            val args = Bundle()
            val fragment = LanguageBottomDialog()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.change_language, container, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
