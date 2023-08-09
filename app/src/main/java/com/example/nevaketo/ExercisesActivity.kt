package com.example.nevaketo

import android.app.ActionBar.LayoutParams
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.util.*

class ExercisesActivity : AppCompatActivity() {

    private val mainLayout: LinearLayout by lazy {
        findViewById(R.id.exercisesMainLayout)
    }

    private val sharedPrefs: SPHelper by lazy {
        SPHelper(getSharedPreferences("USER",0))
    }

    private val layoutParamsButton = LinearLayout.LayoutParams(500, LinearLayout.LayoutParams.MATCH_PARENT).also {
        it.setMargins(0, 36, 0, 36)
    }

    private val layoutParamsView = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT).also {
        it.setMargins(0, 24, 0, 24)
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    override fun onRestart() {
        super.onRestart()
        init()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercises)

        init()
    }

    private fun init() {
        mainLayout.removeAllViews()

        getExercises()
        updateButton()
    }

    private fun getExercises() {
        var index = 0
        while(sharedPrefs.contains("EXERCISE$index")) {
            createExistingExercise(index)
            index++
        }
    }

    private fun createExistingExercise(index: Int) {
        val exerciseView = TextView(this).also { view ->
            view.text = sharedPrefs.getValue("EXERCISE$index")
            view.setTextColor(resources.getColor(R.color.white, theme))
            view.setBackgroundColor(resources.getColor(R.color.salemdark, theme))
            view.gravity = Gravity.CENTER
            view.textSize = 25.0f
            view.isClickable = true
            view.setOnClickListener {
                configureExercise(index)
            }
        }

        mainLayout.addView(exerciseView, layoutParamsView)
    }

    private fun createNewButton() {
        val buttonView = Button(this).also { button ->
            button.setTextColor(resources.getColor(R.color.white))
            button.text = "Новое упражнение"
            button.setBackgroundColor(resources.getColor(R.color.cerise, theme))
            button.setOnClickListener {
                createNewExercise()
                updateButton()
            }
        }

        mainLayout.addView(buttonView, layoutParamsButton)
    }
    private fun createNewExercise() {
        val currentExerciseIndex = sharedPrefs.getMaxValueFromList("EXERCISE") + 1

        sharedPrefs.putValue("EXERCISE$currentExerciseIndex", "Безымянное упражнение")
        sharedPrefs.putValue("EXERCISEDESC$currentExerciseIndex", "")
        sharedPrefs.putValue("EXERCISEENABLED$currentExerciseIndex", false)

        val calendar = Calendar.getInstance()

        sharedPrefs.putValue("EXERCISEHOUR$currentExerciseIndex", calendar.get(Calendar.HOUR_OF_DAY))
        sharedPrefs.putValue("EXERCISEMINUTE$currentExerciseIndex", calendar.get(Calendar.MINUTE))

        sharedPrefs.putValue("EXERCISEPN$currentExerciseIndex", true)
        sharedPrefs.putValue("EXERCISEVT$currentExerciseIndex", true)
        sharedPrefs.putValue("EXERCISESR$currentExerciseIndex", true)
        sharedPrefs.putValue("EXERCISECT$currentExerciseIndex", true)
        sharedPrefs.putValue("EXERCISEPT$currentExerciseIndex", true)
        sharedPrefs.putValue("EXERCISESB$currentExerciseIndex", true)
        sharedPrefs.putValue("EXERCISEVS$currentExerciseIndex", true)

        configureExercise(currentExerciseIndex)
    }

    private fun updateButton() {
        for (i in 0 until mainLayout.childCount) {
            val currentChild = mainLayout.getChildAt(i)

            if (currentChild is Button) {
                mainLayout.removeView(currentChild)
                break
            }
        }
        createNewButton()
    }

    private fun configureExercise(id: Int) {
        val intent = Intent(this, ConfigureExerciseActivity::class.java)
        intent.putExtra("exerciseID", id)
        startActivity(intent)
    }
}