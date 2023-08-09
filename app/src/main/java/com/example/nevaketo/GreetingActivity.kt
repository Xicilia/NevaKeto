package com.example.nevaketo

//import android.annotation.SuppressLint
import android.annotation.SuppressLint
//import android.app.NotificationChannel
//import android.app.NotificationManager
import android.content.Intent
//import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
//import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
/*import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat*/

class GreetingActivity : AppCompatActivity() {

    private val sharedPrefs: SPHelper by lazy { SPHelper(getSharedPreferences("USER",0)) }

    override fun onResume() {
        super.onResume()

        val weight = sharedPrefs.getValue("CURRENTWEIGHT")
        findViewById<Button>(R.id.weightButton).also {
            it.text = if(weight != "NONE") "Вес: $weight кг" else "Вес: -"

        }

        val waist = sharedPrefs.getValue("CURRENTWAIST")
        findViewById<TextView>(R.id.waistButton).also {
            it.text = if(waist != "NONE") "Талия: $waist см" else "Талия: -"

        }
    }

    override fun onRestart() {
        super.onRestart()

        val weight = sharedPrefs.getValue("CURRENTWEIGHT")
        findViewById<Button>(R.id.weightButton).also {
            it.text = if(weight != "NONE") "Вес: $weight кг" else "Вес: -"

        }

        val waist = sharedPrefs.getValue("CURRENTWAIST")
        findViewById<TextView>(R.id.waistButton).also {
            it.text = if(waist != "NONE") "Талия: $waist см" else "Талия: -"

        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_greeting_acitivity)

        println(sharedPrefs.getLongValue("WATERNOTIFICATIONTIME"))

        findViewById<TextView>(R.id.GreetingLabel).text = "Привет, ${sharedPrefs.getValue("NAME")}"

        val weight = sharedPrefs.getValue("CURRENTWEIGHT")
        findViewById<Button>(R.id.weightButton).also {
            it.text = if(weight != "NONE") "Вес: $weight кг" else "Вес: -"
            it.setOnClickListener {
                startActivity(Intent(this,WeightActivity::class.java))
            }
        }

        val waist = sharedPrefs.getValue("CURRENTWAIST")
        findViewById<TextView>(R.id.waistButton).also {
            it.text = if(waist != "NONE") "Талия: $waist см" else "Талия: -"
            it.setOnClickListener {
                startActivity(Intent(this,WaistActivity::class.java))
            }
        }

        findViewById<Button>(R.id.toNotificationsButton).setOnClickListener {
            startActivity(Intent(this,NotificationActivity::class.java))
        }

        findViewById<Button>(R.id.AboutMeButton).setOnClickListener {

            startActivity(Intent(this,AboutMeActivity::class.java))

        }

        findViewById<Button>(R.id.ClearButton).also { button ->

            //if(!BuildConfig.DEBUG) {
            button.visibility = View.GONE
            //} else {
                //button.setOnClickListener { clearName() }
            //}

        }
    }

    private fun clearName() {
        val sharedPrefs = SPHelper(getSharedPreferences("USER",0))

        sharedPrefs.clearValue("NAME")
        sharedPrefs.clearValue("WEIGHT")
        sharedPrefs.clearValue("CURRENTWEIGHT")
        sharedPrefs.clearListedValue("WEIGHTHISTORY")
        sharedPrefs.clearListedValue("WEIGHTHISTORYDATE")
        sharedPrefs.clearValue("CURRENTWAIST")
        sharedPrefs.clearListedValue("WAISTHISTORY")
        sharedPrefs.clearListedValue("WAISTHISTORYDATE")
        sharedPrefs.clearValue("WATER")
        sharedPrefs.clearValue("WATERNOTIFICATIONTIME")
        sharedPrefs.clearValue("WATERNOTIFICATIONTIMETYPE")
        sharedPrefs.clearValue("WATERNOTIFICATIONLASTTIMEHOUR")
        sharedPrefs.clearValue("WATERNOTIFICATIONLASTTIMEMINUTE")
        sharedPrefs.clearListedValue("EAT")

        Toast.makeText(this,"Успешно",Toast.LENGTH_SHORT).show()

        startActivity(Intent(this,MainActivity::class.java))
    }
}