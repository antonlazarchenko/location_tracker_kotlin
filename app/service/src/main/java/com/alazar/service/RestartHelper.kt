package com.alazar.service

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.alazar.authfire.model.UserManagerInterface
import com.alazar.service.di.ServiceComponentProvider
import javax.inject.Inject


class RestartHelper @Inject constructor() : RestartHelperInterface {

    init {
        ServiceComponentProvider.getComponent().inject(this)
    }

    companion object {
        const val TAG = "TrackerRestartService"
    }

    @Inject
    lateinit var user: UserManagerInterface

    @Inject
    lateinit var context: Context

    override fun restartService(serviceClass: Class<*>?) {
        Log.d(TAG, "******* JOB: ATTEMPT TO RESTART TRACKER...")
        if (checkPermission()) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(
                    context,
                    "JOB: Attempt to restart service...",
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(Intent(context, serviceClass))
            } else {
                context.startService(Intent(context, serviceClass))
            }
        }
    }

    override fun sendRestartBroadcast() {
        val broadcastIntent = Intent()
        broadcastIntent.action = "restartService"
        broadcastIntent.setClass(context, ServiceRestart::class.java)
        context.sendBroadcast(broadcastIntent)
    }

    override fun checkPermission(): Boolean {
        Log.d(TAG, "******* checkPermissions")
        var status = true
        val preferences = context.getSharedPreferences(
            context.getString(R.string.shared_preference_name),
            Context.MODE_PRIVATE
        )
        val isServicePrefEnabled =
            preferences.getBoolean(context.getString(R.string.preference_service_param), false)
        if (isServicePrefEnabled
            && user.isAuthenticated()
        ) {
            status = true
        }
        return status
    }
}
