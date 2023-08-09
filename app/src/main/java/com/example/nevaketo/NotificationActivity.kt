package com.example.nevaketo

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.time.LocalDateTime
import java.util.*

class NotificationActivity : AppCompatActivity() {

    val sharedPrefs: SPHelper by lazy{ SPHelper(getSharedPreferences("USER",0)) }

    override fun onRestart() {
        super.onRestart()
        updateWaterElements()
    }

    override fun onResume() {
        super.onResume()
        updateWaterElements()
    }

    override fun onStart() {
        super.onStart()
        updateWaterElements()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifcation)
        updateWaterElements()

        findViewById<Button>(R.id.waterNotificationButton).setOnClickListener {
            startActivity(Intent(this,CreateWaterNotificationActivity::class.java))
        }

        findViewById<Button>(R.id.clearWaterNotificationButton).setOnClickListener {
                val intent = Intent(this,WaterNotificationReceiver::class.java)
                val pendingIntent = if (android.os.Build.VERSION.SDK_INT >= 31) {
                    PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_MUTABLE)
                } else {
                    PendingIntent.getBroadcast(this,0,intent,0)
                }

                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.cancel(pendingIntent)

                sharedPrefs.clearValue("WATERNOTIFICATIONTIME")
                sharedPrefs.clearValue("WATERNOTIFICATIONTIMETYPE")
                sharedPrefs.clearValue("WATERNOTIFICATIONLASTTIMEHOUR")
                sharedPrefs.clearValue("WATERNOTIFICATIONLASTTIMEMINUTE")

                Toast.makeText(this,"Напоминание о питье воды успешно снято!", Toast.LENGTH_LONG).show()
                this.onRestart()

        }

        findViewById<Button>(R.id.eatButton).setOnClickListener {
            startActivity(Intent(this,EatActivity::class.java))
        }


        findViewById<Button>(R.id.toSleepNotification).setOnClickListener {
            startActivity(Intent(this,SleepActivity::class.java))
        }

        findViewById<Button>(R.id.exercisesButton).setOnClickListener {
            startActivity(Intent(this, ExercisesActivity::class.java))
        }

        findViewById<Button>(R.id.medicineButton).setOnClickListener {
            startActivity(Intent(this, MedicineActivity::class.java))
        }

    }

    private fun enableEats() {
        var currentEatIndex = 0
        while (sharedPrefs.contains("EAT$currentEatIndex")) {
            setBroadcastToAlarm(this,currentEatIndex)
            currentEatIndex++
        }
    }

    private fun disableEats() {
        var currentEatIndex = 0
        while (sharedPrefs.contains("EAT$currentEatIndex")) {
            cancelBroadcastFromAlarm(this,currentEatIndex)
            currentEatIndex++
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateWaterElements() {
        val waterEnabled = sharedPrefs.contains("WATERNOTIFICATIONTIME")
        //TODO: instead of checking is time value equal to zero just check is time key exist
        findViewById<Button>(R.id.clearWaterNotificationButton).also { button ->

            if(waterEnabled) {
                button.visibility = View.VISIBLE
            } else {
                button.visibility = View.GONE
            }
        }

        /*findViewById<TextView>(R.id.waterText).also {text ->

            if(waterEnabled) {
                text.visibility = View.VISIBLE

                val lastTimeHour = sharedPrefs.getIntValue("WATERNOTIFICATIONLASTTIMEHOUR").toLong()
                val lastTimeMinute = sharedPrefs.getIntValue("WATERNOTIFICATIONLASTTIMEMINUTE").toLong()

                val delay = sharedPrefs.getLongValue("WATERNOTIFICATIONTIME")
                val type = sharedPrefs.getValue("WATERNOTIFICATIONTIMETYPE")

                var nextTimeHour = lastTimeHour
                var nextTimeMinute = lastTimeMinute

                if (type == "hour") {
                    nextTimeHour += delay / AlarmManager.INTERVAL_HOUR
                } else if (type == "minutes") {
                    val fullMinutes = delay / (AlarmManager.INTERVAL_FIFTEEN_MINUTES / 15)

                    val hours = fullMinutes / 60
                    val actualMinutes = fullMinutes - (hours * 60)

                    nextTimeHour += hours
                    nextTimeMinute += actualMinutes

                    if (nextTimeMinute >= 60) {
                        val extraHours = nextTimeMinute / 60
                        nextTimeHour += extraHours
                        nextTimeMinute -= extraHours * 60
                    }

                    if (nextTimeHour >= 24) {
                        nextTimeHour -= 24
                    }
                }
                text.text = "Следующее напоминание о воде: ${if (nextTimeHour < 10) {
                    "0$nextTimeHour"
                } else nextTimeHour}:${if (nextTimeMinute < 10) "0$nextTimeMinute" else nextTimeMinute}"
            } else {
                text.visibility = View.GONE
            }
        }*/
    }
}