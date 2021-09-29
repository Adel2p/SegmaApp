package com.noob.apps.mvvmcountries.ui.base

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.kaopiz.kprogresshud.KProgressHUD
import com.noob.apps.mvvmcountries.data.UserPreferences
import androidx.lifecycle.asLiveData
import com.framgia.android.emulator.EmulatorDetector
import java.util.*


open class BaseActivity : AppCompatActivity() {
    lateinit var deviceId: String
    private var dialog: KProgressHUD? = null
    lateinit var userPreferences: UserPreferences
    var appLanguage = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        userPreferences = UserPreferences(this)
        userPreferences.getAppLanguage.asLiveData().observeOnce(this, {
            val config = resources.configuration
            var lang = "ar"
            appLanguage = it
            lang = if (appLanguage == "ARABIC")
                "ar"
            else
                "en"
            val locale = Locale(lang)
            Locale.setDefault(locale)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                config.setLocale(locale)
            else
                config.locale = locale

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                createConfigurationContext(config)
            resources.updateConfiguration(config, resources.displayMetrics)
        })
//        EmulatorDetector.with(this)
//            .setCheckTelephony(true)
//            .addPackageName("com.bluestacks")
//            .setDebug(true)
//            .detect {
//                if (it) {
//                    runOnUiThread {
//                        Toast.makeText(
//                            this,
//                            "you cannot use App",
//                            Toast.LENGTH_LONG
//                        ).show()
//                        finish()
//                    }
//
//                } else {
//                    if (isEmulator()) {
//                        Toast.makeText(
//                            this,
//                            "you cannot use App",
//                            Toast.LENGTH_LONG
//                        ).show()
//                        finish()
//                    }
//                }
//            }

    }


    fun hideKeyboard() {
        val imm: InputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view: View? = currentFocus
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showLoader() {
        if (dialog == null) {
            dialog = KProgressHUD.create(this)
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

    fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }
    private fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.contains("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || "google_sdk" == Build.PRODUCT)
    }
    open fun hideSystemUI() {
//        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
//                or View.SYSTEM_UI_FLAG_LOW_PROFILE
//                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                or View.SYSTEM_UI_FLAG_IMMERSIVE
//                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

}