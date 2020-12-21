package com.alazar.tracker

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.alazar.authfire.AuthActivity
import com.alazar.authfire.model.UserManagerInterface
import com.alazar.base.BaseActivity
import com.alazar.map.MapsFragment
import com.alazar.tracker.databinding.ActivityMapBinding
import com.alazar.tracker.di.MainApp
import javax.inject.Inject

class MapActivity : BaseActivity() {

    @Inject
    lateinit var userManager: UserManagerInterface

    private lateinit var binding: ActivityMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MainApp.appComponent.inject(this)

        requestPermissions()
        checkNetworkConnection()
        checkGpsConnection()

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("TAGGGGGGGGG", supportFragmentManager.fragments.toString())
        Log.d("TAGGGGGGGGG", supportFragmentManager.backStackEntryCount.toString())

        if (!userManager.isAuthenticated()) {
            openPostActivity.launch(Intent(this, AuthActivity::class.java))
        } else {
            onActivityResultSuccess()
        }
    }

    override fun onActivityResultSuccess() {
        setContentView(binding.root)
        supportFragmentManager.beginTransaction()
            .replace(binding.frameLayout.id, MapsFragment())
            .commit()
    }

    private fun requestPermissions() {
        requestMultiplePermissions.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,
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