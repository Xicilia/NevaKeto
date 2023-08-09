package com.example.nevaketo

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat

class ConfigureExerciseActivity : AppCompatActivity() {

    private val sharedPrefs: SPHelper by lazy {
        SPHelper(getSharedPreferences("USER", 0))
    }

    private val id: Int by lazy {
        intent.getIntExtra("exerciseID", 0)
    }

    private val name: TextView by lazy {
        findViewById(R.id.exerciseName)
    }

    private val description: TextView by lazy {
        findViewById(R.id.exerciseDescription)
    }

    private val timeField: TextView by lazy {
        findViewById(R.id.exerciseTime)
    }

    override fun onRestart() {
        super.onRestart()

        loadInfo()
    }

    override fun onResume() {
        super.onResume()

        loadInfo()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configure_exercise)

        createChannel(this, "MainChannel", "Main Channel")

        findViewById<Button>(R.id.deleteExercise).setOnClickListener {

            val dialogListener = DialogInterface.OnClickListener { _, selected ->
                when (selected) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        sharedPrefs.clearValueFromList("EXERCISE", id)
                        Toast.makeText(this,"Успешно удалено!",Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
            val eatNameMessagePart = if (name.text.toString() == "") {
                "безымянное упражнение"
            } else {
                "упражнение \"${name.text}\""
            }
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder
                .setMessage("Вы действительно хотите удалить ${eatNameMessagePart}?")
                .setPositiveButton("Да", dialogListener)
                .setNegativeButton("Нет", dialogListener)
                .show()
        }

        findViewById<Button>(R.id.controlExercise).setOnClickListener {
            val controlIntent = Intent(this, ExerciseControlActivity::class.java)
            controlIntent.putExtra("id", id)
            startActivity(controlIntent)
        }

        findViewById<SwitchCompat>(R.id.exerciseEnabler).also { switch ->
            switch.isChecked = sharedPrefs.getBooleanValue("EXERCISEENABLED$id")

            switch.setOnCheckedChangeListener { _, isChecked ->

                val message: String
                if(isChecked) {
                    setExerciseBroadcastToAlarm(this, id)
                    message = "Упражнение включено"
                } else {
                    canselExerciseBroadcastToAlarm(this, id)
                    message = "Упражнение выключено"
                }
                sharedPrefs.putValue("EXERCISEENABLED$id", isChecked)
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }

        val listener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            sharedPrefs.putValue("EXERCISEHOUR$id", hour)
            sharedPrefs.putValue("EXERCISEMINUTE$id", minute)

            if (findViewById<SwitchCompat>(R.id.exerciseEnabler).isChecked) {
                setExerciseBroadcastToAlarm(this, id)
            }

            updateInfo()
            onRestart()
        }

        timeField.setOnClickListener {

            TimePickerDialog(this,
            listener,
            sharedPrefs.getIntValue("EXERCISEHOUR$id"),
            sharedPrefs.getIntValue("EXERCISEMINUTE$id"),
            true).show()
        }

        loadInfo()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        updateInfo()
    }

    private fun loadInfo() {
        val savedName = sharedPrefs.getValue("EXERCISE$id")
        name.text = if (savedName == "Безымянное упражнение") {
            ""
        } else {
            savedName
        }

        val savedDescription = sharedPrefs.getValue("EXERCISEDESC$id")
        description.text = if (savedDescription == "NONE") {
            ""
        } else {
            savedDescription
        }

        val hours = sharedPrefs.getIntValue("EXERCISEHOUR$id")
        val normalizedHours = if(hours >= 10) {
            hours.toString()
        } else {
            "0$hours"
        }
        val minutes = sharedPrefs.getIntValue("EXERCISEMINUTE$id")
        val normalizedMinutes = if (minutes >= 10) {
            minutes.toString()
        } else {
            "0$minutes"
        }
        timeField.text = "${normalizedHours}:$normalizedMinutes"
    }

    @SuppressLint("SetTextI18n")
    private fun updateInfo() {
        sharedPrefs.putValue("EXERCISE$id", if (name.text.isBlank()) "Безымянное упражнение" else name.text.toString())
        sharedPrefs.putValue("EXERCISEDESC$id", description.text.toString())

    }
}