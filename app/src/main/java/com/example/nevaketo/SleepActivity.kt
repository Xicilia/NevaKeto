package com.example.nevaketo

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.AlarmManagerCompat
import java.util.*

class SleepActivity : AppCompatActivity() {

    private val sharedPrefs: SPHelper by lazy {
        SPHelper(getSharedPreferences("USER",0))
    }

    private val timeField: TextView by lazy {
        findViewById(R.id.sleepTimeField)
    }

    private val mainSwitch: SwitchCompat by lazy {
        findViewById(R.id.sleepEnable)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sleep)
        updateTimeText()

        val listener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->

            sharedPrefs.putValue("SLEEPHOUR", hour)
            sharedPrefs.putValue("SLEEPMINUTE", minute)

            updateTimeText()
        }

        timeField.also { view ->
            view.isClickable = true
            view.setOnClickListener {
                TimePickerDialog(this,
                    listener,
                    sharedPrefs.getIntValue("SLEEPHOUR",22),
                    sharedPrefs.getIntValue("SLEEPMINUTE",0),
                    true)
                    .show()
            }
        }

        mainSwitch.isChecked = sharedPrefs.getBooleanValue("SLEEPENABLED")

        mainSwitch.setOnCheckedChangeListener { _, isChecked ->

            if(isChecked) {
                sharedPrefs.putValue("SLEEPENABLED",true)

                val intent = Intent(this,SleepBroadcastReceiver::class.java)
                val pendingIntent = if (android.os.Build.VERSION.SDK_INT >= 31) {
                    PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_MUTABLE)
                } else {
                    PendingIntent.getBroadcast(this,0,intent,0)
                }

                val triggerTime = findTimeToTriggerEat(
                    Calendar.getInstance(),
                    sharedPrefs.getIntValue("SLEEPHOUR"),
                    sharedPrefs.getIntValue("SLEEPMINUTE")
                )

                AlarmManagerCompat.setExact(
                    this.getSystemService(Context.ALARM_SERVICE) as AlarmManager,
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + (triggerTime * (AlarmManager.INTERVAL_FIFTEEN_MINUTES / 15)),
                    pendingIntent
                )
                Toast.makeText(this,"Уведомление о сне успешно включено!",Toast.LENGTH_SHORT).show()
                println("succesfully set sleep with delay $triggerTime minutes")

            } else {
                sharedPrefs.putValue("SLEEPENABLED",false)

                val intent = Intent(this,SleepBroadcastReceiver::class.java)
                val pendingIntent = if (android.os.Build.VERSION.SDK_INT >= 31) {
                    PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_MUTABLE)
                } else {
                    PendingIntent.getBroadcast(this,0,intent,0)
                }

                val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.cancel(pendingIntent)
                Toast.makeText(this,"Уведомление о сне успешно выключено!",Toast.LENGTH_SHORT).show()
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun updateTimeText() {
        if (sharedPrefs.contains("SLEEPHOUR")) {
            val hour = sharedPrefs.getIntValue("SLEEPHOUR")
            val minute = sharedPrefs.getIntValue("SLEEPMINUTE")
            timeField.text = "${if(hour < 10) "0$hour" else hour}:${if(minute < 10) "0$minute" else minute}"
        } else {
            timeField.text = "Время не установлено"
        }
    }
}