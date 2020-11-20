package com.alazar.authfire

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alazar.authfire.databinding.ActivityAuthBinding


class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(binding.frameLayout.id, EmailFragment())
            .commit()
    }
}