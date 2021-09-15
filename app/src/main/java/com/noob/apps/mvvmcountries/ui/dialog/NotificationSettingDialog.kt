package com.noob.apps.mvvmcountries.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.data.UserPreferences
import com.noob.apps.mvvmcountries.databinding.NotificationSettingBinding
import com.noob.apps.mvvmcountries.utils.Constant
import kotlinx.coroutines.launch

class NotificationSettingDialog : BottomSheetDialogFragment() {
    private lateinit var mActivityBinding: NotificationSettingBinding
    private lateinit var userPreferences: UserPreferences

    companion object {

        const val TAG = "NotificationSettingBottomDialog"

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mActivityBinding =
            DataBindingUtil.inflate(inflater, R.layout.notification_setting, container, false)
        return mActivityBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPreferences = UserPreferences(requireContext())
        userPreferences.getFirebaseEnabled.asLiveData().observe(this, {
            if (it) {
                mActivityBinding.englishImg.background =
                    AppCompatResources.getDrawable(requireContext(), R.drawable.ic_blue_circle)
                mActivityBinding.grayImg.background =
                    AppCompatResources.getDrawable(requireContext(), R.drawable.ic_gray_circle)
            } else {
                mActivityBinding.grayImg.background =
                    AppCompatResources.getDrawable(requireContext(), R.drawable.ic_blue_circle)
                mActivityBinding.englishImg.background =
                    AppCompatResources.getDrawable(requireContext(), R.drawable.ic_gray_circle)
            }
        })
        mActivityBinding.txtActiveNotification.setOnClickListener {
            lifecycleScope.launch {
                userPreferences.enableFirebase(true)
            }
            dismiss()
        }
        mActivityBinding.englishImg.setOnClickListener {
            lifecycleScope.launch {
                userPreferences.enableFirebase(true)
            }
            dismiss()
        }
        mActivityBinding.txtCloseNotification.setOnClickListener {
            lifecycleScope.launch {
                userPreferences.enableFirebase(false)
            }
            dismiss()
        }
        mActivityBinding.grayImg.setOnClickListener {
            lifecycleScope.launch {
                userPreferences.enableFirebase(false)
            }
            dismiss()
        }
    }
}
