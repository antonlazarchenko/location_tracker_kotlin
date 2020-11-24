package com.alazar.tracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.alazar.authfire.AuthActivity
import com.alazar.base.core.UserManagerInterface
import com.alazar.tracker.di.MainApp
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var userManager: UserManagerInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MainApp().getComponent().inject(this)

        if (!userManager.isAuthorized()) {
            openPostActivity.launch(Intent(this, AuthActivity::class.java))
        }

        setContentView(R.layout.activity_main)
    }

    private val openPostActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Result OK", Toast.LENGTH_SHORT).show()
            }
        }
}