package com.example.nevaketo

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import org.w3c.dom.Text

class WeightHistory : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight_history)

        initializeHistory()

    }

    override fun onRestart() {

        super.onRestart()
        initializeHistory()

    }

    private fun initializeHistory() {
        val mainLayout = findViewById<LinearLayout>(R.id.mainLayout)
        mainLayout.removeAllViews() //to avoid bugs

        //layouts use this params
        val layoutParamsForLayout = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT)

        //views use this params
        val layoutParamsForTextView = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.MATCH_PARENT)



        val sharedPrefs = SPHelper(getSharedPreferences("USER",0))

        val views: ArrayList<LinearLayout> = arrayListOf()
        var lastUsedWeightIndex = 0

        while(sharedPrefs.contains("WEIGHTHISTORY$lastUsedWeightIndex")) {

            LinearLayout(this).also{ layout ->
                layout.orientation = LinearLayout.HORIZONTAL

                TextView(this).also {valueView ->
                    valueView.setTextColor(resources.getColor(R.color.white))
                    valueView.textSize = 25.0f
                    valueView.text = "${sharedPrefs.getValue("WEIGHTHISTORY$lastUsedWeightIndex")}кг"
                    layout.addView(valueView,layoutParamsForTextView)
                }

                TextView(this).also {EmptySpaceView ->
                    layout.addView(EmptySpaceView,layoutParamsForTextView)
                }

                TextView(this).also { dateView ->
                    dateView.setTextColor(resources.getColor(R.color.white))
                    dateView.textSize = 25.0f
                    dateView.text = ", Дата: ${sharedPrefs.getValue("WEIGHTHISTORYDATE$lastUsedWeightIndex")}"
                    layout.addView(dateView,layoutParamsForTextView)
                }
                views.add(layout)
            }

            lastUsedWeightIndex++
        }
        //TODO: make this shit look better
        views.reverse()
        views.forEach {
            mainLayout.addView(it,layoutParamsForLayout)
        }
    }

    fun back(view: android.view.View) {
        startActivity(Intent(this,GreetingActivity::class.java))
    }
}