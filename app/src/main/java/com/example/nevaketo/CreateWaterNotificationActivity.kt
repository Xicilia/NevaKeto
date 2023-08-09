package com.example.nevaketo

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.widget.*
import androidx.core.app.AlarmManagerCompat
import java.sql.Time
import java.util.*

class CreateWaterNotificationActivity : AppCompatActivity() {

    companion object {
        const val hour = AlarmManager.INTERVAL_HOUR
        const val minutes = AlarmManager.INTERVAL_FIFTEEN_MINUTES / 15
    }

    private  val sharedPrefs: SPHelper by lazy {
        SPHelper(getSharedPreferences("USER",0))
    }

    private val skipTimesContainer: LinearLayout by lazy {
        findViewById(R.id.skipTimesContainer)
    }

    private val layoutParamsButton = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
        it.setMargins(0,24,0,0)
    }
    private val layoutParamsInsideContainer = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT).also {
        it.setMargins(0,0,0,0)
    }

    private lateinit var listener: TimePickerDialog.OnTimeSetListener

    private var selectedId = -1
    private var selectedPoint = "-" // Start or End

    override fun onRestart() {
        super.onRestart()

        selectedId = -1
        selectedPoint = "-" // Start or End

        initContainer()
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_water_notification)

        listener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            val otherPoint = if (selectedPoint == "START") {
                "END"
            } else {
                "START"
            }

            if(otherPoint == "START") {

                val startHour = sharedPrefs.getIntValue("WATERSKIP${otherPoint}HOUR$selectedId")
                val startMinute = sharedPrefs.getIntValue("WATERSKIP${otherPoint}MINUTE$selectedId")
                if ((startHour > hour) || (startHour == hour && startMinute > minute)) {
                    sharedPrefs.putValue("WATERSKIP${selectedPoint}HOUR$selectedId", startHour)
                    sharedPrefs.putValue("WATERSKIP${selectedPoint}MINUTE$selectedId", startMinute)

                    sharedPrefs.putValue("WATERSKIP${otherPoint}HOUR$selectedId", hour)
                    sharedPrefs.putValue("WATERSKIP${otherPoint}MINUTE$selectedId", minute)

                } else {
                    sharedPrefs.putValue("WATERSKIP${selectedPoint}HOUR$selectedId", hour)
                    sharedPrefs.putValue("WATERSKIP${selectedPoint}MINUTE$selectedId", minute)
                }

            } else {

                val endHour = sharedPrefs.getIntValue("WATERSKIP${otherPoint}HOUR$selectedId")
                val endMinute = sharedPrefs.getIntValue("WATERSKIP${otherPoint}MINUTE$selectedId")
                if((endHour < hour) || (endHour == hour && endMinute < minute)) {

                    sharedPrefs.putValue("WATERSKIP${selectedPoint}HOUR$selectedId", endHour)
                    sharedPrefs.putValue("WATERSKIP${selectedPoint}MINUTE$selectedId", endMinute)

                    sharedPrefs.putValue("WATERSKIP${otherPoint}HOUR$selectedId", hour)
                    sharedPrefs.putValue("WATERSKIP${otherPoint}MINUTE$selectedId", minute)
                } else {
                    sharedPrefs.putValue("WATERSKIP${selectedPoint}HOUR$selectedId", hour)
                    sharedPrefs.putValue("WATERSKIP${selectedPoint}MINUTE$selectedId", minute)
                }
            }
            onRestart()
        }

        initContainer()

        findViewById<Button>(R.id.clearSkipTimes).setOnClickListener {
            sharedPrefs.clearListedValue("WATERSKIPSTARTHOUR")
            sharedPrefs.clearListedValue("WATERSKIPSTARTMINUTE")
            sharedPrefs.clearListedValue("WATERSKIPENDHOUR")
            sharedPrefs.clearListedValue("WATERSKIPENDMINUTE")
            onRestart()
        }

        val spinner = findViewById<Spinner>(R.id.TimeSelectingSpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.TimeSelect,
            R.layout.spinner_item
        ).also { adapter ->

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

        }

        findViewById<ImageButton>(R.id.ApplyWaterButton).setOnClickListener {
            // creating water notification using parameters from activity

            val timeStringRepresent = spinner.selectedItem.toString() // time type in string

            val usingTimeType = if (timeStringRepresent == "Час") {
                hour
            } else {
                minutes
            }

            val timeValue = findViewById<TextView>(R.id.ValueLabel) // value that need to wait
                .text
                .toString()
                .toInt()


            if(timeStringRepresent == "Час") {

                if (timeValue < 1) {

                    Toast.makeText(this,
                        "Значение для часов не может быть меньше одного часа, для этого используйте минуты",
                        Toast.LENGTH_LONG).show()
                    return@setOnClickListener

                }
            }

            createNotificationChannel()

            val intent = Intent(this,WaterNotificationReceiver::class.java)

            val pendingIntent = if (Build.VERSION.SDK_INT >= 31) {
                PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_MUTABLE)
            } else {
                PendingIntent.getBroadcast(this,0,intent,0)
            }

            val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val time = (usingTimeType * timeValue) // Long value that contains how many milliseconds will alarm wait
            sharedPrefs.putValue("WATERNOTIFICATIONTIME",time)
            sharedPrefs.putValue("WATERNOTIFICATIONTIMETYPE", if (usingTimeType == hour) "hour" else "minutes")

            val calendar = Calendar.getInstance()
            sharedPrefs.putValue("WATERNOTIFICATIONLASTTIMEHOUR", calendar.get(Calendar.HOUR_OF_DAY))
            sharedPrefs.putValue("WATERNOTIFICATIONLASTTIMEMINUTE", calendar.get(Calendar.MINUTE))

            AlarmManagerCompat.setExact(alarmManager, AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + time, pendingIntent)

            Toast.makeText(this,
                "Напоминание о питье воды установлено!",
                Toast.LENGTH_SHORT).show()

            val backIntent = Intent(this, NotificationActivity::class.java)
            backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(Intent(backIntent))
            }
        }

    private fun createSkipTime(id: Int, startTime: String, endTime: String) {
        val container = LinearLayout(this)
        container.orientation = LinearLayout.HORIZONTAL
        container.gravity = Gravity.CENTER
        container.isClickable = true

        val calendar = Calendar.getInstance()

        fun createTimeField(time: String, point: String): TextView {

            return TextView(ContextThemeWrapper(this, R.style.commonLabels)).also {
                it.setTextColor(resources.getColor(R.color.cerise, theme))
                it.text = time
                it.isClickable = true
                it.setOnClickListener {
                    selectedId = id
                    selectedPoint = point
                    TimePickerDialog(
                        this,
                        listener,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                }
            }
        }

        container.addView(createTimeField(startTime, "START"))
        container.addView(
            TextView(ContextThemeWrapper(this, R.style.commonLabelsSmall)).also {
                it.text = " ПО "
            }
        )
        container.addView(createTimeField(endTime, "END"))
        skipTimesContainer.addView(container,layoutParamsButton)
    }

    private fun initContainer() {
        skipTimesContainer.removeAllViews()

        var currentIndex = 0
        while (sharedPrefs.contains("WATERSKIPSTARTHOUR$currentIndex")) {
            val startTimeNormalised = getTimeNormalized(
                sharedPrefs.getIntValue("WATERSKIPSTARTHOUR$currentIndex"),
                sharedPrefs.getIntValue("WATERSKIPSTARTMINUTE$currentIndex")
            )
            val endTimeNormalised = getTimeNormalized(
                sharedPrefs.getIntValue("WATERSKIPENDHOUR$currentIndex"),
                sharedPrefs.getIntValue("WATERSKIPENDMINUTE$currentIndex")
            )

            createSkipTime(
                currentIndex,
                startTimeNormalised,
                endTimeNormalised
            )
            currentIndex++
        }

        skipTimesContainer.addView(
            ImageButton(this).also {
                it.setImageResource(R.mipmap.button_plus_foreground)
                it.setBackgroundColor(resources.getColor(R.color.transparent, theme))
                it.setOnClickListener {
                    val calendar = Calendar.getInstance()
                    val newIndex = sharedPrefs.getMaxValueFromList("WATERSKIPSTARTHOUR") + 1
                    sharedPrefs.putValue("WATERSKIPSTARTHOUR$newIndex", calendar.get(Calendar.HOUR_OF_DAY))
                    sharedPrefs.putValue("WATERSKIPSTARTMINUTE$newIndex", calendar.get(Calendar.MINUTE))

                    sharedPrefs.putValue("WATERSKIPENDHOUR$newIndex", calendar.get(Calendar.HOUR_OF_DAY))
                    sharedPrefs.putValue("WATERSKIPENDMINUTE$newIndex", calendar.get(Calendar.MINUTE))

                    onRestart()
                }
            }
        )
    }

    private fun createNotificationChannel() {
        //Because android wants it

        val name = "Water channel"
        val descriptionText = "channel for water"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("WaterChannel", name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        println("created channel")
    }
}