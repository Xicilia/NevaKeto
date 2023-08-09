package com.example.nevaketo

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class InputWaistActivity : AppCompatActivity() {

    private val dateField: TextView by lazy {
        findViewById(R.id.dateFieldWaist)
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_waist)
        val isReg = intent.hasExtra("IsReg")

        dateField.also {

            it.text = SimpleDateFormat("dd.MM.yyyy").format(Date())

            //if (BuildConfig.DEBUG) {
            it.isClickable = true

            val listener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
                val actualMonthValue = month + 1
                it.text = "${if (day >= 10) day else "0$day"}.${if(actualMonthValue > 10) actualMonthValue else "0$actualMonthValue"}.${year}"
            }

            it.setOnClickListener {
                val currentDate = LocalDate.now()
                DatePickerDialog(
                    this,
                    R.style.Theme_NevaKeto,
                    listener,
                    currentDate.year,
                    currentDate.monthValue - 1,
                    currentDate.dayOfMonth).show()
            }
            //} else {
             //   it.visibility = View.INVISIBLE
            //}
        }

        findViewById<ImageButton>(R.id.applyWaist).also {
            if (!isReg) {
                it.setImageResource(R.mipmap.button_ok_white_foreground)
            }

            it.setOnClickListener {

            val waistSize = findViewById<TextView>(R.id.waistField).text.toString()

            val sharedPrefs = SPHelper(getSharedPreferences("USER",0))
            val lastWaistIndex =  sharedPrefs.getMaxValueFromList("WAISTHISTORY") + 1

            sharedPrefs.putValue("CURRENTWAIST",waistSize)
            sharedPrefs.putValue("WAISTHISTORY$lastWaistIndex",waistSize)
            //if (BuildConfig.DEBUG) {
            sharedPrefs.putValue("WAISTHISTORYDATE$lastWaistIndex", dateField.text.toString())
           // } else {
           //     sharedPrefs.putValue("WAISTHISTORYDATE$lastWaistIndex",
          //          SimpleDateFormat("dd.MM.yyyy").format(Date()))
           // }

            if(isReg) {
                val intent = Intent(this,WeightRegistrationActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.putExtra("TITLE","Последний штрих!")
                startActivity(intent)
            } else {
                val intent = Intent(this,WaistActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        }
        }
    }


}