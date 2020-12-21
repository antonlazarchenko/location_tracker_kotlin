package com.alazar.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.alazar.service.di.ServiceComponentProvider
import javax.inject.Inject

class ServiceRestart : BroadcastReceiver() {

    @Inject
    lateinit var restartHelper: RestartHelper

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("Broadcast Listened", "Service tried to stop")

        ServiceComponentProvider.getComponent().inject(this)

        restartHelper.restartService(TrackerService::class.java)
    }
}