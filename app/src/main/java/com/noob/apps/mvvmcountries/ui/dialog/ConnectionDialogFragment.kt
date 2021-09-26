package com.noob.apps.mvvmcountries.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.ConnectionDialogBinding
import com.noob.apps.mvvmcountries.viewmodels.SharedViewModel

class ConnectionDialogFragment : DialogFragment() {
    private lateinit var mActivityBinding: ConnectionDialogBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var operation: String

    companion object {

        const val TAG = "ConnectionDialogFragment"

        private const val KEY_OPERATION = "KEY_OPERATION"

        fun newInstance(operation: String): ConnectionDialogFragment {
            val args = Bundle()
            args.putString(KEY_OPERATION, operation)
            val fragment = ConnectionDialogFragment()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            android.R.style.Theme_Black_NoTitleBar_Fullscreen
        )
        arguments?.let {
            operation =
                (it.getString(KEY_OPERATION))!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mActivityBinding =
            DataBindingUtil.inflate(inflater, R.layout.connection_dialog, container, false)
        // Inflate the layout for this fragment
        return mActivityBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        mActivityBinding.retry.setOnClickListener {
            sharedViewModel.sendOperation(operation)
            dismiss()
        }
    }


}