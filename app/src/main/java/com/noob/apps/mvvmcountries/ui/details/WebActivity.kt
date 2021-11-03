package com.noob.apps.mvvmcountries.ui.details

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.mediarouter.media.MediaRouter
import com.noob.apps.mvvmcountries.R
import com.noob.apps.mvvmcountries.databinding.ActivityWebBinding
import com.noob.apps.mvvmcountries.ui.base.BaseActivity
import com.noob.apps.mvvmcountries.utils.Constant
import kotlinx.android.synthetic.main.activity_web.*
import android.webkit.WebSettings
import android.webkit.WebView

import android.webkit.WebViewClient







class WebActivity : BaseActivity() {
    //   private lateinit var mActivityBinding: ActivityWebBinding
    private var url = ""
    private val TAG = "LectureFolderActivity"
    private var mMediaRouter: MediaRouter? = null
    private val DISCOVERY_FRAGMENT_TAG = "DiscoveryFragment"
    private val mMediaRouterCB: MediaRouter.Callback = object : MediaRouter.Callback() {
        override fun onRouteAdded(router: MediaRouter, route: MediaRouter.RouteInfo) {
            Log.d(
                TAG,
                "onRouteAdded: route=$route"
            )

        }

        override fun onRouteChanged(router: MediaRouter, route: MediaRouter.RouteInfo) {
            Log.d(
                TAG,
                "onRouteChanged: route=$route"
            )

        }


        override fun onRouteRemoved(router: MediaRouter, route: MediaRouter.RouteInfo) {
            Log.d(
                TAG,
                "onRouteRemoved: route=$route"
            )

        }

        override fun onRouteSelected(router: MediaRouter, route: MediaRouter.RouteInfo) {
            Log.d(
                TAG,
                "onRouteSelected: route=$route"
            )
             if (route.connectionState == 2)
                showBlockDialog("You Cannot run App on Screen Mirroring")


        }

        override fun onRouteUnselected(router: MediaRouter, route: MediaRouter.RouteInfo) {
            Log.d(
                TAG,
                "onRouteUnselected: route=$route"
            )

            hideBlockDialog()
        }

        override fun onRouteVolumeChanged(router: MediaRouter, route: MediaRouter.RouteInfo) {
        }

        override fun onRoutePresentationDisplayChanged(
            router: MediaRouter,
            route: MediaRouter.RouteInfo
        ) {
            Log.d(
                TAG,
                "onRoutePresentationDisplayChanged: route=$route"
            )
            if (route.connectionState == 2)
                showBlockDialog("You Cannot run App on Screen Mirroring")

        }

        override fun onProviderAdded(router: MediaRouter, provider: MediaRouter.ProviderInfo) {
        }

        override fun onProviderRemoved(router: MediaRouter, provider: MediaRouter.ProviderInfo) {
        }

        override fun onProviderChanged(router: MediaRouter, provider: MediaRouter.ProviderInfo) {
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // mActivityBinding =
        //     DataBindingUtil.setContentView(this, R.layout.activity_web)
        setContentView(R.layout.activity_web)
        url = intent.getStringExtra(Constant.WEB_URL).toString()
        val settings: WebSettings = myWebView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        myWebView.loadUrl(url)
        myWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {

                //Block all URL accesses.
                return false
            }
        }

    }

    override fun onResume() {
        super.onResume()
        mMediaRouter = MediaRouter.getInstance(this)
        val fm = supportFragmentManager
        val fragment: DiscoveryFragment?
        fragment = DiscoveryFragment()
        fragment.setCallback(mMediaRouterCB)
        fm.beginTransaction().add(fragment, DISCOVERY_FRAGMENT_TAG).commit()
    }
}