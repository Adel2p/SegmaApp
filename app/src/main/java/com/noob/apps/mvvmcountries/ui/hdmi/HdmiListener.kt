package com.noob.apps.mvvmcountries.ui.hdmi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class HdmiListener : BroadcastReceiver() {
    override fun onReceive(ctxt: Context?, receivedIt: Intent) {
        val action = receivedIt.action
        if (action == HDMIINTENT) {
            val state = receivedIt.getBooleanExtra("state", false)
            if (state) {
                ctxt!!.sendBroadcast(Intent("INTERNET_LOST"));

            }
        }
    }

    companion object {
        private const val HDMIINTENT = "android.intent.action.HDMI_PLUGGED"
    }
}