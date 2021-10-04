package com.noob.apps.mvvmcountries.ui.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.UserBlockDialogBinding
import com.noob.apps.mvvmcountries.models.LectureDetails

class BlockUserDialog : DialogFragment() {
    private lateinit var mActivityBinding: UserBlockDialogBinding
    private var Title = ""

    companion object {

        const val TAG = "LectureWatchDialog"


        fun newInstance(title: String): BlockUserDialog {
            val args = Bundle()
            args.putString("title", title)
            val fragment = BlockUserDialog()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            Title =
                (it.getString("title").toString())
        }

    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            dialog.getWindow()
                ?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mActivityBinding =
            DataBindingUtil.inflate(inflater, R.layout.user_block_dialog, container, false)
        // Inflate the layout for this fragment
        return mActivityBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Title.isNotEmpty())
            mActivityBinding.txtCheckConnection.text = Title
        mActivityBinding.retry.setOnClickListener {
            requireActivity().finishAffinity()
            dismiss()
        }
    }
}