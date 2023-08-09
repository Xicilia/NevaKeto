package com.example.nevaketo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class InputsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inputs)

        findViewById<Button>(R.id.SetWeightButton).setOnClickListener {
            val intent = Intent(this,WeightRegistrationActivity::class.java)

            //to not create two activities for capturing weight just change WeightRegistrationActivity title using Extra
            intent.putExtra("TITLE","Взвешивание")
            startActivity(intent)
        }

        findViewById<Button>(R.id.ToWeightHistory).setOnClickListener {
            intent = Intent(this,InputHistoryActivity::class.java)
            intent.putExtra("VALUESKEY","WEIGHTHISTORY")
            intent.putExtra("VALUESDATE","WEIGHTHISTORYDATE")
            intent.putExtra("POSTFIX","кг")
            startActivity(intent)
        }

        findViewById<Button>(R.id.toWaistInput).setOnClickListener {
            startActivity(Intent(this,InputWaistActivity::class.java))
        }

        findViewById<Button>(R.id.ToWaistHistory).setOnClickListener {
            intent = Intent(this,InputHistoryActivity::class.java)
            intent.putExtra("VALUESKEY","WAISTHISTORY")
            intent.putExtra("VALUESDATE","WAISTHISTORYDATE")
            intent.putExtra("POSTFIX","см")
            startActivity(intent)
        }
    }
}