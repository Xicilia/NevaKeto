package com.example.nevaketo

import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.widget.SwitchCompat

class ExerciseControlActivity : AppCompatActivity() {

    private val id: Int by lazy {
        intent.getIntExtra("id", 0)
    }

    private val sharedPrefs: SPHelper by lazy {
        SPHelper(getSharedPreferences("USER", 0))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_control)

        println("current day is ${getDayOfTheWeek(Calendar.getInstance())}")

        findViewById<SwitchCompat>(R.id.PNenabler).also { switch ->
            switch.isChecked = sharedPrefs.getBooleanValue("EXERCISEPN$id")

            switch.setOnCheckedChangeListener { _, isChecked ->
                sharedPrefs.putValue("EXERCISEPN$id", isChecked)
            }
        }

        findViewById<SwitchCompat>(R.id.VTenabler).also { switch ->
            switch.isChecked = sharedPrefs.getBooleanValue("EXERCISEVT$id")

            switch.setOnCheckedChangeListener { _, isChecked ->
                sharedPrefs.putValue("EXERCISEVT$id", isChecked)
            }
        }

        findViewById<SwitchCompat>(R.id.SRenabler).also { switch ->
            switch.isChecked = sharedPrefs.getBooleanValue("EXERCISESR$id")

            switch.setOnCheckedChangeListener { _, isChecked ->
                sharedPrefs.putValue("EXERCISESR$id", isChecked)
            }
        }

        findViewById<SwitchCompat>(R.id.CTenabler).also { switch ->
            switch.isChecked = sharedPrefs.getBooleanValue("EXERCISECT$id")

            switch.setOnCheckedChangeListener { _, isChecked ->
                sharedPrefs.putValue("EXERCISECT$id", isChecked)
            }
        }

        findViewById<SwitchCompat>(R.id.PTenabler).also { switch ->
            switch.isChecked = sharedPrefs.getBooleanValue("EXERCISEPT$id")

            switch.setOnCheckedChangeListener { _, isChecked ->
                sharedPrefs.putValue("EXERCISEPT$id", isChecked)
            }
        }

        findViewById<SwitchCompat>(R.id.SBenabler).also { switch ->
            switch.isChecked = sharedPrefs.getBooleanValue("EXERCISESB$id")

            switch.setOnCheckedChangeListener { _, isChecked ->
                sharedPrefs.putValue("EXERCISESB$id", isChecked)
            }
        }

        findViewById<SwitchCompat>(R.id.VSenabler).also { switch ->
            switch.isChecked = sharedPrefs.getBooleanValue("EXERCISEVS$id")

            switch.setOnCheckedChangeListener { _, isChecked ->
                sharedPrefs.putValue("EXERCISEVS$id", isChecked)
            }
        }
    }
}