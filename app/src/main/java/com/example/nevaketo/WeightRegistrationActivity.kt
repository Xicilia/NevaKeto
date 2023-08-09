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
import android.widget.Toast
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class WeightRegistrationActivity : AppCompatActivity() {

    private val dateField: TextView by lazy {
        findViewById(R.id.dateField)
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight_registration)

        val title = intent.getStringExtra("TITLE")
        findViewById<TextView>(R.id.WeightRegistartionLabelMain).text = title

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
            //    it.visibility = View.INVISIBLE
            //}
        }

        findViewById<ImageButton>(R.id.registerWeightButton).setOnClickListener {

            val weight = findViewById<TextView>(R.id.WeightField).text.toString()

            weight.toDouble().also {

                if(it <= 0 || it >= 1000) {

                    Toast.makeText(this,"Неправильное значение веса",Toast.LENGTH_LONG).show()
                    return@setOnClickListener

                }
            }

            val sharedPrefs = SPHelper(getSharedPreferences("USER",0))

            val lastWeightInHistoryIndex = sharedPrefs.getMaxValueFromList("WEIGHTHISTORY") + 1

            sharedPrefs.putValue("WEIGHTHISTORY$lastWeightInHistoryIndex",weight)
            //if(BuildConfig.DEBUG) {
            sharedPrefs.putValue("WEIGHTHISTORYDATE$lastWeightInHistoryIndex",dateField.text.toString())
            //} else{
            //    sharedPrefs.putValue("WEIGHTHISTORYDATE$lastWeightInHistoryIndex",SimpleDateFormat("dd.MM.yyyy").format(Date()))
            //}
            sharedPrefs.putValue("CURRENTWEIGHT",weight)


            val activity = if (intent.getBooleanExtra("IsReg", true)) {
                GreetingActivity::class.java
            } else {
                WeightActivity::class.java
            }
            val intent = Intent(this,activity)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

        }
    }
}