package com.example.nevaketo

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class InputHistoryActivity : AppCompatActivity() {

    private val valuesKey: String by lazy {
        if (intent.hasExtra("VALUESKEY")) {
            intent.getStringExtra("VALUESKEY")!!
        } else {
            "NONE"
        }
    }
    private val dateKey: String by lazy {
        if(intent.hasExtra("VALUESDATE")) {
            intent.getStringExtra("VALUESDATE")!!
        } else {
            "NONE"
        }
    }

    private val valuePostfix: String by lazy {
        if(intent.hasExtra("POSTFIX")) {
            intent.getStringExtra("POSTFIX")!!
        } else {
            ""
        }
    }

    private val deleteButton: Button by lazy {
        findViewById(R.id.deleteInputButton)
    }

    private val layout: LinearLayout by lazy {
        findViewById(R.id.mainLayout)
    }

    private val sharedPrefs: SPHelper by lazy {
        SPHelper(getSharedPreferences("USER",0))
    }

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
        setContentView(R.layout.activity_input_history)
        init()
    }

    private fun init(selectedView: Int = -1) {
        layout.removeAllViews()

        deleteButton.visibility = View.GONE
        var currentViewIndex = 0
        while (sharedPrefs.contains(valuesKey + currentViewIndex.toString())) {
            if(currentViewIndex == selectedView){
                createView(currentViewIndex, true)
            } else {
                createView(currentViewIndex)
            }
            currentViewIndex++
        }
    }

    private fun setDeleteButtonOn(index: Int) {
        if (deleteButton.visibility == View.GONE) {
            deleteButton.visibility = View.VISIBLE
        }
        deleteButton.setOnClickListener {
            sharedPrefs.clearValueFromList(valuesKey,index)
            sharedPrefs.clearValueFromList(dateKey,index)
            Toast.makeText(this,"Успешно удалено", Toast.LENGTH_SHORT).show()
            init()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun createView(index: Int, isSelected: Boolean = false) {
        val stringifyIndex = index.toString()

        val value = sharedPrefs.getValue(valuesKey + stringifyIndex)
        val date = sharedPrefs.getValue(dateKey + stringifyIndex)

        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        params.setMargins(0,0,0,60)

        TextView(this).also { view ->
            view.text = date
            if(isSelected) {
                view.setTextColor(resources.getColor(R.color.gray))
            } else {
                view.setTextColor(resources.getColor(R.color.white))
            }
            view.gravity = Gravity.CENTER
            view.textSize = 25.0f
            view.isClickable = true
            view.setOnClickListener {
                init(index)
                setDeleteButtonOn(index)
            }
            layout.addView(view)
        }

        TextView(this).also { view ->
            view.text = "Значение: ${value}$valuePostfix"
            view.gravity = Gravity.CENTER
            if(isSelected) {
                view.setTextColor(resources.getColor(R.color.gray))
            } else {
                view.setTextColor(resources.getColor(R.color.white))
            }
            view.textSize = 25.0f
            layout.addView(view,params)
        }
    }
}