package com.alazar.tracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.alazar.authfire.AuthActivity
import com.alazar.authfire.model.UserModel
import com.alazar.base.core.UserManagerInterface
import com.alazar.tracker.databinding.ActivityMainBinding
import com.alazar.tracker.di.MainApp
import javax.inject.Inject

class MainActivity : AppCompatActivity(), View.OnClickListener {

    @Inject
    lateinit var userManager: UserManagerInterface

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MainApp().getComponent().inject(this)

        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.btnSignOut.setOnClickListener(this)
        binding.btnStart.setOnClickListener(this)
        binding.btnStop.setOnClickListener(this)

        if (!userManager.isAuthorized()) {
            openPostActivity.launch(Intent(this, AuthActivity::class.java))
        } else {
            loadLayout()
        }
    }

    private val openPostActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK)
                loadLayout()
        }

    private fun loadLayout() {
        setContentView(binding.root)
    }

    override fun onClick(v: View) {
        when (v.id) {
            binding.btnSignOut.id -> {
                UserModel().signOut()
                recreate()
            }
            binding.btnStart.id -> {
            }
            binding.btnStop.id -> {
            }
        }
    }
}