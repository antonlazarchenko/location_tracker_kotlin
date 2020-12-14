package com.alazar.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.ConnectivityManager
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.location.LocationManagerCompat
import com.alazar.base.util.NetworkUtil

interface BaseActivityInterface {
    fun loadLayout()
}

open class BaseActivity : AppCompatActivity(), BaseActivityInterface {

    companion object {
        const val TAG = "BaseActivity"
    }

    protected val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                Log.e(TAG, "${it.key} = ${it.value}")
            }
        }

    protected val openPostActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadLayout()
            }
        }

    protected open fun checkNetworkConnection() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val isConnected = NetworkUtil(connectivityManager).isConnected()

        if (!isConnected) {
            val builder = AlertDialog.Builder(this)

            builder.setMessage(R.string.request_enable_wifi)
                .setPositiveButton(
                    R.string.enable
                ) { dialog, id -> startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) }
                .setNegativeButton(R.string.cancel) { dialog, id -> finish() }
            val alert = builder.create()
            alert.show()
        }
    }

    protected open fun checkGpsConnection() {
        var isConnectedLocation = false
        val locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        if (LocationManagerCompat.isLocationEnabled(locationManager)) {
            isConnectedLocation = true
        }
        val builder = AlertDialog.Builder(this)
        if (!isConnectedLocation) {
            builder.setMessage(R.string.request_enable_gps)
                .setPositiveButton(
                    R.string.enable
                ) { dialog, id -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
                .setNegativeButton(R.string.cancel) { dialog, id -> finish() }
            val alert = builder.create()
            alert.show()
        }
    }

    override fun loadLayout() {}
}