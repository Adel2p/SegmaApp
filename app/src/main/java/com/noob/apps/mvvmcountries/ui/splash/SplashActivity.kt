package com.noob.apps.mvvmcountries.ui.splash

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.data.DatabaseBuilder
import com.noob.apps.mvvmcountries.data.DatabaseHelperImpl
import com.noob.apps.mvvmcountries.models.RefreshTokenModel
import com.noob.apps.mvvmcountries.ui.base.BaseActivity
import com.noob.apps.mvvmcountries.ui.dialog.BlockUserDialog
import com.noob.apps.mvvmcountries.ui.dialog.ConnectionDialogFragment
import com.noob.apps.mvvmcountries.ui.home.HomeActivity
import com.noob.apps.mvvmcountries.ui.login.LoginActivity
import com.noob.apps.mvvmcountries.ui.welcome.UniversityActivity
import com.noob.apps.mvvmcountries.utils.Constant
import com.noob.apps.mvvmcountries.utils.ViewModelFactory
import com.noob.apps.mvvmcountries.viewmodels.SplashViewModel
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.telephony.TelephonyManager
import android.media.AudioManager.ACTION_HDMI_AUDIO_PLUG
import android.content.IntentFilter
import android.widget.Toast


class SplashActivity : BaseActivity() {
    private var isSaved = false
    private var isloggedin = false
    private lateinit var splashViewModel: SplashViewModel
    private var refreshToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash2)
        val CanMirror: Int = Settings.Secure.getInt(
            this.contentResolver,
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
            0
        )
        splashViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                application,
                DatabaseHelperImpl(DatabaseBuilder.getInstance(applicationContext))
            )
        ).get(SplashViewModel::class.java)

        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter.isEnabled)
            return BlockUserDialog.newInstance("Please turn off Bluetooth\n")
                .show(supportFragmentManager, BlockUserDialog.TAG)

        if (Settings.Secure.getInt(getContentResolver(), Settings.Secure.ADB_ENABLED, 0) == 1) {
            return BlockUserDialog.newInstance("Please turn off usb debugging\n")
                .show(supportFragmentManager, BlockUserDialog.TAG)
        }
        if (checkEmulatorFiles())
            return BlockUserDialog.newInstance("App can't run on Emulators")
                .show(supportFragmentManager, BlockUserDialog.TAG)
        if (isEmulator()) {
            return BlockUserDialog.newInstance("App can't run on Emulators")
                .show(supportFragmentManager, BlockUserDialog.TAG)
        }
        val tm = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        val networkOperator = tm.networkOperatorName
        if ("Android" == networkOperator) {
            return BlockUserDialog.newInstance("App can't run on Emulators")
                .show(supportFragmentManager, BlockUserDialog.TAG)
        } else {
            userPreferences.savedLogginedFlow.asLiveData().observeOnce(this, {
                isloggedin = it
                if (isloggedin)
                    readISSaved()
                else
                    openLogin()

            })
        }


    }

    private fun readToken() {
        userPreferences.getRefreshToken.asLiveData().observeOnce(this, {
            //    refreshToken = it
            if (refreshToken.isNotEmpty())
                initTokenObservers()
            else
                navigate()


        })
    }

    private fun readISSaved() {
        userPreferences.getUniversityData.asLiveData().observeOnce(this, {
            isSaved = it
            readToken()
        })
    }

    private fun initTokenObservers() {
        splashViewModel.updateToken(RefreshTokenModel(Constant.REFRESH_TOKEN, refreshToken))
        splashViewModel.updateTokenResponse.observeOnce(this, { kt ->
            if (kt != null) {
                navigate()
            }
        })
        splashViewModel.mShowResponseError.observeOnce(this, {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finishAffinity()
        })
        splashViewModel.mShowProgressBar.observe(this, { bt ->
            if (bt) {
                showLoader()
            } else {
                hideLoader()
            }

        })
        splashViewModel.mShowNetworkError.observeOnce(this, {
            if (it != null) {
                ConnectionDialogFragment.newInstance(Constant.RETRY_LOGIN)
                    .show(supportFragmentManager, ConnectionDialogFragment.TAG)
            }

        })
    }

    private fun navigate() {
        CoroutineScope(Dispatchers.IO).launch {
            delay(TimeUnit.SECONDS.toMillis(0))
            withContext(Dispatchers.Main) {
                if (isloggedin) {
                    if (isSaved) {
                        openHome()
                    } else {
                        openUniversity()
                    }
                } else {
                    openLogin()

                }


            }
        }
    }

    private fun openHome() {
        val intent = Intent(this@SplashActivity, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun openUniversity() {
        val intent =
            Intent(this@SplashActivity, UniversityActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun openLogin() {
        val intent = Intent(this@SplashActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private val eventReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            // pause video
            val action = intent.action
            when (action) {
                ACTION_HDMI_AUDIO_PLUG ->                     // EXTRA_AUDIO_PLUG_STATE: 0 - UNPLUG, 1 - PLUG
                    return BlockUserDialog.newInstance("Please plug off HDMI cable")
                        .show(supportFragmentManager, BlockUserDialog.TAG)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(eventReceiver)
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter()
        filter.addAction(ACTION_HDMI_AUDIO_PLUG)
        registerReceiver(eventReceiver, filter)
    }

}
