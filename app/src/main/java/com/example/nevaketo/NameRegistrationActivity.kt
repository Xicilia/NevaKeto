package com.example.nevaketo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView

class NameRegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name_registration)

        findViewById<ImageButton>(R.id.regContinue).setOnClickListener {
            val sharedPrefs = SPHelper(getSharedPreferences("USER",0))

            sharedPrefs.putValue("NAME", findViewById<TextView>(R.id.NameField).text.toString())

            val intent = Intent(this,HeightRegistrationActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }
}

