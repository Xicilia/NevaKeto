package com.example.nevaketo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

class HeightRegistrationActivity : AppCompatActivity() {
    /**
     * Height register.
     *
     * In this activity non register user will input his height
     * */


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_height_registration)

        findViewById<ImageButton>(R.id.RegisterHeightButton).setOnClickListener {

            val height = findViewById<TextView>(R.id.HeightField).text.toString().toInt()

            if(height <= 0) {

                Toast.makeText(this,"Неправильное значение роста",Toast.LENGTH_LONG).show()
                return@setOnClickListener

            }

            val sharedPrefs = SPHelper(getSharedPreferences("USER",0))
            sharedPrefs.putValue("HEIGHT",height)

            val intent = Intent(this,InputWaistActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.putExtra("IsReg", 1)
            startActivity(intent)
        }
    }
}