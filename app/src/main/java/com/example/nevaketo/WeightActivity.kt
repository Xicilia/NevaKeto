package com.example.nevaketo

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import kotlin.math.max

class WeightActivity : AppCompatActivity() {
    private val mainLayout: LinearLayout by lazy {
        findViewById(R.id.mainLayout)
    }

    private val deleteButton: Button by lazy {
        findViewById(R.id.deleteButton)
    }

    private val currentWeight: TextView by lazy {
        findViewById(R.id.currentWeight)
    }

    private val sharedPrefs: SPHelper by lazy {
        SPHelper(getSharedPreferences("USER", 0))
    }

    private val layoutParamsForContainer = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT).also {
        it.setMargins(0,0,0,24)
    }

    private val layoutParamsForViewsInContainer = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT).also {
        it.setMargins(16,16,16,16)
    }

    private var selectedView = -1

    override fun onRestart() {
        super.onRestart()

        init()
    }

    override fun onResume() {
        super.onResume()

        init()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight)

        init()

        findViewById<Button>(R.id.deleteButton).setOnClickListener {
            if (selectedView == -1) {
                Toast.makeText(this, "Сначала выберите значение", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val maxValue = sharedPrefs.getMaxValueFromList("WEIGHTHISTORY")

            val actualId = maxValue - selectedView
            sharedPrefs.clearValueFromList("WEIGHTHISTORY", actualId)
            sharedPrefs.clearValueFromList("WEIGHTHISTORYDATE", actualId)

            if (selectedView == 0) {
                sharedPrefs.putValue("CURRENTWEIGHT", sharedPrefs.getValue("WEIGHTHISTORY${maxValue - 1}"))
            }

            onRestart()
        }

        findViewById<Button>(R.id.addButton).setOnClickListener {
            val intent = Intent(this,WeightRegistrationActivity::class.java)

            intent.putExtra("TITLE","Взвешивание")
            intent.putExtra("IsReg", false)
            startActivity(intent)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        val currentWeightValue = sharedPrefs.getValue("CURRENTWEIGHT")
        currentWeight.text = "Текущее значение: ${if (currentWeightValue != "NONE") "${currentWeightValue}кг" else "-"}"

        mainLayout.removeAllViews()
        selectedView = -1

        deleteButton.visibility = View.INVISIBLE

        val maxIndex = sharedPrefs.getMaxValueFromList("WEIGHTHISTORY")
        for(index in maxIndex downTo 0) {
            createNewHistoryValue(index, maxIndex)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun createNewHistoryValue(id: Int, maxIndexInList: Int) {
        val actualId = maxIndexInList - id

        val container = LinearLayout(this)
        container.orientation = LinearLayout.HORIZONTAL
        container.gravity = Gravity.CENTER

        container.addView( // date view
            TextView(this).also {
                it.text = "${sharedPrefs.getValue("WEIGHTHISTORYDATE$id")}:"
                it.textSize = 25F
                it.setTextColor(resources.getColor(R.color.white))
                it.gravity = Gravity.CENTER
            }, layoutParamsForViewsInContainer
        )

        container.addView( // value view
            TextView(this).also {
                it.text = "${sharedPrefs.getValue("WEIGHTHISTORY$id")}кг"
                it.textSize = 25F
                it.setTextColor(resources.getColor(R.color.white))
                it.gravity = Gravity.CENTER
            }, layoutParamsForViewsInContainer
        )

        container.isClickable = true
        container.setOnClickListener {
            deleteButton.visibility = View.VISIBLE
            setNewSelectedView(container, actualId)
        }
        container.background = ResourcesCompat.getDrawable(resources, R.drawable.rectangle, theme)

        mainLayout.addView(container, layoutParamsForContainer)
    }

    private fun setNewSelectedView(newView: View, id: Int) {
        mainLayout.getChildAt(selectedView).also {
            if (it is LinearLayout) {
                it.background = ResourcesCompat.getDrawable(resources, R.drawable.rectangle, theme)
            }
        }

        newView.background = ResourcesCompat.getDrawable(resources, R.drawable.rectangle_pink, theme)
        selectedView = id
    }
}