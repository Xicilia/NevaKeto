package com.example.nevaketo

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.AlarmManagerCompat
import org.w3c.dom.Text
import java.util.*

class EatConfigureActivity : AppCompatActivity() {

    private val eatId: Int by lazy {
        intent.getIntExtra("EATKEY",0)
    }

    private val eatField: TextView by lazy {
        findViewById(R.id.eatNameEdit)
    }

    private val eatDesciptionField: TextView by lazy {
        findViewById(R.id.eatDescriptionField)
    }

    private val timeField: TextView by lazy {
        findViewById(R.id.eatTimeLabel)
    }

    private val sharedPrefs: SPHelper by lazy {
        SPHelper(getSharedPreferences("USER",0))
    }

    override fun onRestart() {
        super.onRestart()

        updateInfo()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        updateInfoInSharedPrefs()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eat_configure)
        createChannel()
        updateInfo()
        findViewById<Button>(R.id.applyEatConfigureButton).also { button ->
            button.setOnClickListener {
                sharedPrefs.putValue("EAT$eatId",eatField.text.toString())

                Toast.makeText(this,"Успешно изменено!",Toast.LENGTH_SHORT).show()
            }
            button.visibility = View.GONE
        }

        findViewById<Button>(R.id.deleteEatButton).setOnClickListener {
            val dialogListener = DialogInterface.OnClickListener {_, selected ->
                when (selected) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        sharedPrefs.clearValueFromList("EAT",eatId)
                        Toast.makeText(this,"Успешно удалено!",Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
            val eatNameMessagePart = if (eatField.text.toString() == "") {
                "безымянный прием пищи"
            } else {
                "прием пищи \"${eatField.text}\""
            }
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder
                .setMessage("Вы действительно хотите удалить ${eatNameMessagePart}?")
                .setPositiveButton("Да", dialogListener)
                .setNegativeButton("Нет", dialogListener)
                .show()
        }

        val listener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            sharedPrefs.putValue("EATHOUR$eatId",hour)
            sharedPrefs.putValue("EATMINUTE$eatId",minute)

            if (findViewById<SwitchCompat>(R.id.isEnabledSwitch).isChecked) { // if eat is enabled recreate broadcast
                setBroadcastToAlarm(this, eatId)
            }

            updateInfoInSharedPrefs()
            onRestart()
        }

        findViewById<TextView>(R.id.eatTimeLabel).setOnClickListener {

            TimePickerDialog(this,
                listener,
                sharedPrefs.getIntValue("EATHOUR$eatId"),
                sharedPrefs.getIntValue("EATMINUTE$eatId"),
                true)
                .show()
        }

        findViewById<SwitchCompat>(R.id.isEnabledSwitch).also {switch ->
            switch.isChecked = sharedPrefs.getIntValue("EATENABLED$eatId") == 1

            switch.setOnCheckedChangeListener { _, isChecked ->

                if(isChecked) {
                    setBroadcastToAlarm(this,eatId)
                    sharedPrefs.putValue("EATENABLED$eatId",1)
                } else {
                    cancelBroadcastFromAlarm(this,eatId)
                    sharedPrefs.putValue("EATENABLED$eatId",0)
                }
            }

        }
    }

    private fun updateInfoInSharedPrefs() {
        val eatNameText = eatField.text.toString()
        if(eatNameText.isBlank()) {
            sharedPrefs.putValue("EAT$eatId",resources.getString(R.string.unnamedEat))
        } else {
            sharedPrefs.putValue("EAT$eatId",eatNameText)
        }
        sharedPrefs.putValue("EATDESC$eatId",eatDesciptionField.text.toString())
    }

    @SuppressLint("SetTextI18n")
    private fun updateInfo() {
        val eatName = sharedPrefs.getValue("EAT$eatId")
        eatField.text = if(eatName == resources.getString(R.string.unnamedEat)) {
            ""
        } else {
            eatName
        }

        val eatDescription = sharedPrefs.getValue("EATDESC$eatId")
        eatDesciptionField.text = if(eatDescription == "NONE") {
            ""
        } else {
            eatDescription
        }

        val hour = sharedPrefs.getIntValue("EATHOUR$eatId")
        val minute = sharedPrefs.getIntValue("EATMINUTE$eatId")
        //if hour or minute value is less than 10 add zero to start
        timeField.text = "${if(hour < 10) "0$hour" else hour}:${if(minute < 10) "0$minute" else minute}"
    }

    private fun createChannel() {
        //Because android wants it

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "MainChannel"
            val descriptionText = "Main channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("MainChannel", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            println("created channel")
        }
    }
}