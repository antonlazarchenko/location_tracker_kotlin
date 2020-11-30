package com.alazar.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ServiceRestart : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("Broadcast Listened", "Service tried to stop")
        RestartHelper().restartService(context, TrackerService::class.java)
    }
}