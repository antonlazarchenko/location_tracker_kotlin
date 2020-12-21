package provider

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import com.alazar.base.R
import com.alazar.base.core.NetworkProvider
import com.alazar.base.di.BaseApp
import javax.inject.Inject

class NetworkWrapper @Inject constructor() : NetworkProvider {

    @Inject
    lateinit var context: Context

    private var connectivityManager: ConnectivityManager

    init {
        BaseApp.appComponent.inject(this)

        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override fun isConnected() : Boolean {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork
            val actNw = connectivityManager.getNetworkCapabilities(nw)
            (actNw != null
                    && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)))
        } else {
            @Suppress("DEPRECATION")
            val nwInfo = connectivityManager.activeNetworkInfo
            @Suppress("DEPRECATION")
            nwInfo != null && nwInfo.isConnected
        }
    }


    override fun runNetworkConnectionMonitor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

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