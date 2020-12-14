package com.alazar.base.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import com.alazar.base.R

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

    companion object {
        fun runNetworkConnectionMonitor(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                    override fun onLost(network: Network) {
                        super.onLost(network)
                        Toast.makeText(context, context.getString(R.string.internet_lost), Toast.LENGTH_LONG).show()
                    }

                    override fun onUnavailable() {
                        super.onUnavailable()
                        Toast.makeText(context, context.getString(R.string.internet_unavailable), Toast.LENGTH_LONG).show()
                    }
                })
            }
        }
    }
}