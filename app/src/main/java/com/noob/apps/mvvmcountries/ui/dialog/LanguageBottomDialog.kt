package com.noob.apps.mvvmcountries.ui.dialog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.data.UserPreferences
import com.noob.apps.mvvmcountries.databinding.AboutSegmaDialogBinding
import com.noob.apps.mvvmcountries.databinding.ChangeLanguageBinding
import com.noob.apps.mvvmcountries.ui.home.HomeActivity
import com.noob.apps.mvvmcountries.ui.welcome.UniversityActivity
import com.noob.apps.mvvmcountries.utils.Constant
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.TimeUnit

class LanguageBottomDialog : BottomSheetDialogFragment() {
    private lateinit var mActivityBinding: ChangeLanguageBinding
    lateinit var locale: Locale
    private lateinit var userPreferences: UserPreferences

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
        mActivityBinding =
            DataBindingUtil.inflate(inflater, R.layout.change_language, container, false)
        // Inflate the layout for this fragment
        return mActivityBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPreferences = UserPreferences(requireContext())
        userPreferences.getAppLanguage.asLiveData().observe(this, {
            if (it == Constant.ARABIC) {
                mActivityBinding.englishImg.background =
                    AppCompatResources.getDrawable(requireContext(), R.drawable.ic_gray_circle)
                mActivityBinding.arImage.background =
                    AppCompatResources.getDrawable(requireContext(), R.drawable.ic_blue_circle)
            } else {
                mActivityBinding.arImage.background =
                    AppCompatResources.getDrawable(requireContext(), R.drawable.ic_gray_circle)
                mActivityBinding.englishImg.background =
                    AppCompatResources.getDrawable(requireContext(), R.drawable.ic_blue_circle)
            }
        })
        mActivityBinding.txtArabic.setOnClickListener {
            switchArabic()

        }
        mActivityBinding.arImage.setOnClickListener {
            switchArabic()

        }
        mActivityBinding.englishImg.setOnClickListener {
            switchEnglish()

        }

        mActivityBinding.txtEnglish.setOnClickListener {
            switchEnglish()

        }

    }

    private fun switchEnglish() {
        lifecycleScope.launch {
            userPreferences.saveLanguage(Constant.ENGLISH)
        }
        setAppLocale(requireContext(), "en")
    }

    private fun switchArabic() {
        lifecycleScope.launch {
            userPreferences.saveLanguage(Constant.ARABIC)
        }
        setAppLocale(requireContext(), "ar")
    }


    private fun setAppLocale(context: Context, language: String) {
        CoroutineScope(Dispatchers.IO).launch {
            delay(TimeUnit.SECONDS.toMillis(0))
            val locale = Locale(language)
            Locale.setDefault(locale)
            val config = context.resources.configuration
            config.setLocale(locale)
            context.createConfigurationContext(config)
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            val refresh = Intent(
                requireActivity(),
                HomeActivity::class.java
            )
            startActivity(refresh)
        }

    }
}
