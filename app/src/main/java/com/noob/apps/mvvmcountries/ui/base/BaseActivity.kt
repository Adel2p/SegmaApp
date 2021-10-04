package com.noob.apps.mvvmcountries.ui.base

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
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
    private var sIsProbablyRunningOnEmulator: Boolean? = null
    //  private lateinit var caster: Caster

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
        EmulatorDetector.with(this)
            .setCheckTelephony(true)
            .addPackageName("com.bluestacks")
            .setDebug(true)
            .detect {
                if (it) {
                    runOnUiThread {
                        Toast.makeText(
                            this,
                            "you cannot use App",
                            Toast.LENGTH_LONG
                        ).show()
                     //   finish()
                    }

                }
            }
//        caster = Caster.create(this)
//        if (caster.isConnected)
//            finishAffinity()
//        caster.setOnConnectChangeListener(object : Caster.OnConnectChangeListener {
//            override fun onConnected() {
//                finishAffinity()
//            }
//
//            override fun onDisconnected() {
//            }
//        })

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

    fun isEmulator(): Boolean {
        var result = sIsProbablyRunningOnEmulator
        if (result != null)
            return result
        // Android SDK emulator
        result = (Build.FINGERPRINT.startsWith("google/sdk_gphone_")
                && Build.FINGERPRINT.endsWith(":user/release-keys")
                && Build.MANUFACTURER == "Google" && Build.PRODUCT.startsWith("sdk_gphone_") && Build.BRAND == "google"
                && Build.MODEL.startsWith("sdk_gphone_"))
                //
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                //bluestacks
                || "QC_Reference_Phone" == Build.BOARD && !"Xiaomi".equals(
            Build.MANUFACTURER,
            ignoreCase = true
        ) //bluestacks
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.HOST == "Build2" //MSI App Player
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || Build.PRODUCT == "google_sdk"
        // another Android SDK emulator check

        sIsProbablyRunningOnEmulator = result
        return result
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