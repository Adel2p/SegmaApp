package com.noob.apps.mvvmcountries.ui.base


import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
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
import java.io.File
import java.util.*
import android.util.Log
import androidx.mediarouter.app.MediaRouteDiscoveryFragment
import androidx.mediarouter.media.MediaRouter
import com.noob.apps.mvvmcountries.ui.dialog.BlockUserDialog
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception


open class BaseActivity2 : AppCompatActivity() {
    lateinit var deviceId: String
    private var dialog: KProgressHUD? = null
    lateinit var userPreferences: UserPreferences
    var appLanguage = ""
    private var sIsProbablyRunningOnEmulator: Boolean? = null
    private var mMediaRouter: MediaRouter? = null
    private val DISCOVERY_FRAGMENT_TAG = "DiscoveryFragment"
    var isrouted = false
    private lateinit var blockUserDialog: BlockUserDialog

    private val PIPES = arrayOf(
        "/dev/socket/qemud",
        "/dev/qemu_pipe"
    )
    private val X86_FILES = arrayOf(
        "ueventd.android_x86.rc",
        "x86.prop",
        "ueventd.ttVM_x86.rc",
        "init.ttVM_x86.rc",
        "fstab.ttVM_x86",
        "fstab.vbox86",
        "init.vbox86.rc",
        "ueventd.vbox86.rc"
    )
    private val ANDY_FILES = arrayOf(
        "fstab.andy",
        "ueventd.andy.rc"
    )
    private val NOX_FILES = arrayOf(
        "fstab.nox",
        "init.nox.rc",
        "ueventd.nox.rc"
    )
    private val mMediaRouterCB: MediaRouter.Callback = object : MediaRouter.Callback() {
        // Return a custom callback that will simply log all of the route events
        // for demonstration purposes.
        override fun onRouteAdded(router: MediaRouter, route: MediaRouter.RouteInfo) {
        }

        override fun onRouteChanged(router: MediaRouter, route: MediaRouter.RouteInfo) {
        }

        override fun onRouteRemoved(router: MediaRouter, route: MediaRouter.RouteInfo) {
        }

        override fun onRouteSelected(router: MediaRouter, route: MediaRouter.RouteInfo) {
//            mPlayer.updatePresentation()
//            mSessionManager.setPlayer(mPlayer)
//            mSessionManager.unsuspend()
//            registerRemoteControlClient()
//            updateUi()

        }

        override fun onRouteUnselected(router: MediaRouter, route: MediaRouter.RouteInfo) {
            isrouted = true
            return BlockUserDialog.newInstance("You Cannot run App on Screen Mirroring")
                .show(
                    supportFragmentManager,
                    BlockUserDialog.TAG
                )            //  mPlayer.updatePresentation()
            //  mPlayer.release()
        }

        override fun onRouteVolumeChanged(router: MediaRouter, route: MediaRouter.RouteInfo) {
            // Log.d(MainActivity.TAG, "onRouteVolumeChanged: route=$route")
        }

        override fun onRoutePresentationDisplayChanged(
            router: MediaRouter,
            route: MediaRouter.RouteInfo
        ) {
            //  Log.d(MainActivity.TAG, "onRoutePresentationDisplayChanged: route=$route")
            //  mPlayer.updatePresentation()
            isrouted = true
            return BlockUserDialog.newInstance("You Cannot run App on Screen Mirroring")
                .show(supportFragmentManager, BlockUserDialog.TAG)
        }

        override fun onProviderAdded(router: MediaRouter, provider: MediaRouter.ProviderInfo) {
            // Log.d(MainActivity.TAG, "onRouteProviderAdded: provider=$provider")
        }

        override fun onProviderRemoved(router: MediaRouter, provider: MediaRouter.ProviderInfo) {
            // Log.d(MainActivity.TAG, "onRouteProviderRemoved: provider=$provider")
        }

        override fun onProviderChanged(router: MediaRouter, provider: MediaRouter.ProviderInfo) {
            //   Log.d(MainActivity.TAG, "onRouteProviderChanged: provider=$provider")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        );
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
        mMediaRouter = MediaRouter.getInstance(this)

        val fm = supportFragmentManager
        var fragment: BaseActivity.DiscoveryFragment?
        fragment = BaseActivity.DiscoveryFragment()
        fragment.setCallback(mMediaRouterCB)
        fm.beginTransaction().add(fragment, DISCOVERY_FRAGMENT_TAG).commit()

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
                || Build.HARDWARE == "vbox86"
                || Build.PRODUCT == "vbox86p"
                || Build.HARDWARE.lowercase(Locale.getDefault()).contains("nox")
                || Build.MODEL.lowercase(Locale.getDefault()).contains("droid4x")
                || Build.HARDWARE == "goldfish"
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.PRODUCT.lowercase(Locale.getDefault()).contains("nox")
                || Build.BOARD.lowercase(Locale.getDefault()).contains("nox")
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

    private fun checkFiles(targets: Array<String>): Boolean {
        for (pipe in targets) {
            val file = File(pipe)
            if (file.exists()) {
                return true
            }
        }
        return false
    }

    fun checkEmulatorFiles(): Boolean {
        return checkFiles(ANDY_FILES)
                || checkFiles(NOX_FILES)
                || checkFiles(X86_FILES)
                || checkFiles(PIPES)
    }

    protected open fun hideSystemUI() {
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    fun checkHDMI(): String {
        var result = ""
        try {
            val file = File("/sys/class/display/display0.hdmi/connect")
            val `in`: InputStream = FileInputStream(file)
            val re = ByteArray(32768)
            var read = 0
            while (`in`.read(re, 0, 32768).also { read = it } != -1) {
                val string = String(re, 0, read)
                Log.v("String_whilecondition", "HDMI state = $string")
                result = string
            }
            `in`.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return result
    }

    fun isHdmiSwitchSet(): Boolean {

        // The file '/sys/devices/virtual/switch/hdmi/state' holds an int -- if it's 1 then an HDMI device is connected.
        // An alternative file to check is '/sys/class/switch/hdmi/state' which exists instead on certain devices.
        var switchFile = File("/sys/devices/virtual/switch/hdmi/state")
        if (!switchFile.exists()) {
            switchFile = File("/sys/class/switch/hdmi/state")
        }
        return try {
            val switchFileScanner = Scanner(switchFile)
            val switchValue = switchFileScanner.nextInt()
            switchFileScanner.close()
            switchValue > 0
        } catch (e: Exception) {
            false
        }
    }

    private val bluetoothChangeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action

            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(
                    BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR
                )
                when (state) {
                    BluetoothAdapter.STATE_ON -> return BlockUserDialog.newInstance("Please turn off Bluetooth\n")
                        .show(supportFragmentManager, BlockUserDialog.TAG)
                    BluetoothAdapter.STATE_TURNING_ON -> return BlockUserDialog.newInstance("Please turn off Bluetooth\n")
                        .show(supportFragmentManager, BlockUserDialog.TAG)
                }
            }
        }
    }
    private val eventReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            // pause video
            val action = intent.action
            when (action) {
                AudioManager.ACTION_HDMI_AUDIO_PLUG ->                     // EXTRA_AUDIO_PLUG_STATE: 0 - UNPLUG, 1 - PLUG
                    return BlockUserDialog.newInstance("Please plug off HDMI cable")
                        .show(supportFragmentManager, BlockUserDialog.TAG)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(bluetoothChangeReceiver, filter)
        val filter2 = IntentFilter()
        filter2.addAction(AudioManager.ACTION_HDMI_AUDIO_PLUG)
        registerReceiver(eventReceiver, filter2)
    }

    override fun onDestroy() {
        super.onDestroy()
        //    unregisterReceiver(broadcastReceiver)
    }

    class DiscoveryFragment : MediaRouteDiscoveryFragment() {
        private lateinit var mCallback: MediaRouter.Callback
        fun setCallback(cb: MediaRouter.Callback) {
            mCallback = cb
        }

        override fun onCreateCallback(): MediaRouter.Callback {
            return mCallback
        }

        override fun onPrepareCallbackFlags(): Int {
            // Add the CALLBACK_FLAG_UNFILTERED_EVENTS flag to ensure that we will
            // observe and log all route events including those that are for routes
            // that do not match our selector.  This is only for demonstration purposes
            // and should not be needed by most applications.
            return super.onPrepareCallbackFlags() or MediaRouter.CALLBACK_FLAG_UNFILTERED_EVENTS
        }

        companion object {
            private const val TAG = "DiscoveryFragment"
        }
    }

    fun showBlockDialog(msg: String) {
        blockUserDialog = BlockUserDialog.newInstance(msg)
        blockUserDialog.show(
            supportFragmentManager,
            BlockUserDialog.TAG
        )
    }


    fun hideBlockDialog() {
        if (::blockUserDialog.isInitialized) {
            finish();
            startActivity(intent);
        }

    }
}