package com.alazar.base.util

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

class NetworkUtil constructor(private var cm: ConnectivityManager) {

    fun isConnected() : Boolean {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = cm.activeNetwork
            val actNw = cm.getNetworkCapabilities(nw)
            (actNw != null
                    && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)))
        } else {
            @Suppress("DEPRECATION")
            val nwInfo = cm.activeNetworkInfo
            @Suppress("DEPRECATION")
            nwInfo != null && nwInfo.isConnected
        }
    }
}