package com.alazar.service

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.alazar.authfire.model.UserManagerInterface
import com.alazar.base.util.NetworkUtil
import com.alazar.service.data.LocationData
import com.alazar.service.di.ServiceApp
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.orhanobut.hawk.Hawk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TrackerService : Service(), LocationListener {
    companion object {
        private const val TAG = "TrackerService"
        private const val NOTIFICATION_PROCESS_ID = 1024
        private const val LOCATION_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY
        private const val LOCATION_INTERVAL = 60 //seconds
    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private lateinit var connectivityManager: ConnectivityManager

    private lateinit var ringtone: Ringtone

    @Inject
    lateinit var user: UserManagerInterface

    private lateinit var dbFirebaseModel: DbFirebaseModel

    private lateinit var workRequest: PeriodicWorkRequest

    override fun onCreate() {
        super.onCreate()

        ServiceApp().getComponent().inject(this)

        dbFirebaseModel = DbFirebaseModel(user.getUserId().toString())
        Log.d(TAG, user.getUserId().toString())

        connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        fusedLocationProviderClient = getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                onLocationChanged(locationResult.lastLocation)
            }
        }

        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        ringtone = RingtoneManager.getRingtone(applicationContext, notification)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        addNotificationAndStartForeground()

        runLocationTransfer()
        addWorkers()

        Log.d(TAG, "===== SERVICE START")
        return START_STICKY
    }


    private fun addNotificationAndStartForeground() {
        val name = getString(R.string.app_name)
        val description = "Service running..."

        val pendingIntent = PendingIntent.getActivity(this, 0, Intent(), 0)
        val notificationBuilder: Notification.Builder

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_PROCESS_ID.toString(),
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = getString(R.string.notification_channel_desc)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            notificationBuilder = Notification.Builder(this, NOTIFICATION_PROCESS_ID.toString())
            notificationBuilder.setContentTitle(name)
                .setContentText(description)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
            notificationManager.notify(NOTIFICATION_PROCESS_ID, notificationBuilder.build())
        } else {
            @Suppress("DEPRECATION")
            notificationBuilder = Notification.Builder(this)
            notificationBuilder.setContentTitle(name)
                .setContentText(description)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
        }
        startForeground(NOTIFICATION_PROCESS_ID, notificationBuilder.build())
    }

    override fun onDestroy() {
        super.onDestroy()

        fusedLocationProviderClient.removeLocationUpdates(locationCallback)

        WorkManager.getInstance(this).cancelWorkById(workRequest.id)

        Log.d(TAG, "===== SERVICE STOP")
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        Log.d(TAG, "TASK REMOVED")
        RestartHelper().sendRestartBroadcast(this)
        super.onTaskRemoved(rootIntent)
    }

    private fun runLocationTransfer() {

        val locationRequest = LocationRequest.create().apply {
            interval = 1000 * LOCATION_INTERVAL.toLong()
            fastestInterval = 1000 * LOCATION_INTERVAL.toLong()
            priority = LOCATION_PRIORITY
        }

        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        val locationSettingsRequest: LocationSettingsRequest = builder.build()
        val settingsClient: SettingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
        }
    }


    override fun onLocationChanged(location: Location) {
        Log.d(TAG, "===== SERVICE LOCATION CHANGED")

        if (!RestartHelper().checkPermission(this)) {
            stopSelf()
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            Log.d(TAG, "====== SERVICE STOPPED by itself")
        } else if (NetworkUtil(getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).isConnected()) {

            dbFirebaseModel.saveLocation(location)

            // test using sound notifications
            try {
                ringtone.play()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            Toast.makeText(
                this,
                getString(R.string.latitude) + location.latitude + getString(R.string.longitude) + location.longitude,
                Toast.LENGTH_SHORT
            ).show()
        } else {

            GlobalScope.launch(Dispatchers.IO) {
                saveToLocalStorage(location)
            }
        }
    }

    private fun saveToLocalStorage(location: Location) {
        Hawk.init(this).build()
        val locationData = LocationData(location)
        var count: Long = 0
        if (Hawk.count() > count) {
            count = Hawk.count()
        }
        while (Hawk.contains(count.toString())) {
            count++
        }
        Hawk.put(count.toString(), locationData)
        Log.d(TAG, "HAWK /// Saved to local storage. COUNT = " + Hawk.count())
    }

    private fun addWorkers() {
        workRequest = PeriodicWorkRequest.Builder(
            FirebaseWorker::class.java, 15, TimeUnit.MINUTES, 2, TimeUnit.MINUTES
        )
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            FirebaseWorker.TAG,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

}
