package com.example.nevaketo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val sharedPrefs = SPHelper(getSharedPreferences("USER",0))
        if(sharedPrefs.contains("NAME")) {
            createActivity(GreetingActivity())
        } else {
            createActivity(NameRegistrationActivity())
        }
        finish()

    }
    private fun createActivity(Activity : AppCompatActivity) {
        val intent = Intent(this,Activity::class.java)
        startActivity(intent)

    }
}

