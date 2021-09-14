package com.noob.apps.mvvmcountries.ui.base

import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.fragment.app.Fragment
import com.kaopiz.kprogresshud.KProgressHUD
import com.noob.apps.mvvmcountries.utils.UserPreferences

open class BaseFragment : Fragment() {
    lateinit var userPreferences: UserPreferences
    lateinit var deviceId: String
    private var dialog: KProgressHUD? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPreferences = UserPreferences(requireActivity())
        deviceId = Settings.Secure.getString(requireActivity().contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun showLoader() {
        if (dialog == null) {
            dialog = KProgressHUD.create(requireActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show()
        } else {
            dialog?.show()
        }
    }

    fun hideLoader() {
        dialog?.dismiss()
    }
}