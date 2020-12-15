package com.alazar.tracker

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.alazar.authfire.AuthActivity
import com.alazar.authfire.model.UserManagerInterface
import com.alazar.base.BaseActivity
import com.alazar.service.TrackerService
import com.alazar.tracker.databinding.ActivityMainBinding
import com.alazar.tracker.di.MainApp
import javax.inject.Inject

class MainActivity : BaseActivity(), View.OnClickListener {

    @Inject
    lateinit var userManager: UserManagerInterface

    private lateinit var binding: ActivityMainBinding

    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MainApp().getComponent().inject(this)

        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.btnSignOut.setOnClickListener(this)
        binding.btnStart.setOnClickListener(this)
        binding.btnStop.setOnClickListener(this)

        preferences = getSharedPreferences(getString(R.string.shared_preference_name), MODE_PRIVATE)

        requestPermissions()
        checkNetworkConnection()
        checkGpsConnection()

        if (!userManager.isAuthenticated()) {
            openPostActivity.launch(Intent(this, AuthActivity::class.java))
        } else {
            loadLayout()
            changeStatus(getIsRunningPreference())
        }
    }


    override fun loadLayout() {
        setContentView(binding.root)
        changeStatus(getIsRunningPreference())
    }

    override fun onClick(v: View) {
        when (v.id) {
            binding.btnSignOut.id -> {
                userManager.signOut()
                stopService()
                recreate()
            }
            binding.btnStart.id -> {
                saveServiceStatusPref(true)
            }
            binding.btnStop.id -> {
                saveServiceStatusPref(false)
            }
        }
    }

    private fun saveServiceStatusPref(status: Boolean) {
        val editor = preferences.edit()
        editor.putBoolean(getString(R.string.preference_service_param), status)
        editor.apply()
        changeStatus(status)
    }

    private fun changeStatus(isRunningPref: Boolean) {
        if (isRunningPref) {
            startService()
            binding.status.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
            binding.status.text = getString(R.string.running)
            binding.btnStart.visibility = View.INVISIBLE
            binding.btnStop.visibility = View.VISIBLE
        } else {
            stopService()
            binding.status.setTextColor(ContextCompat.getColor(this, R.color.colorDanger))
            binding.status.text = getString(R.string.stopped)
            binding.btnStart.visibility = View.VISIBLE
            binding.btnStop.visibility = View.INVISIBLE
        }
    }


    private fun startService() {
        startService(Intent(this, TrackerService::class.java))
    }

    private fun stopService() {
        stopService(Intent(this, TrackerService::class.java))
    }

    private fun getIsRunningPreference(): Boolean {
        return preferences.getBoolean(getString(R.string.preference_service_param), false)
    }

    private fun requestPermissions() {
        requestMultiplePermissions.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
            )
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            requestMultiplePermissions.launch(
                arrayOf(
                    Manifest.permission.FOREGROUND_SERVICE,
                )
            )
        }

    }
}