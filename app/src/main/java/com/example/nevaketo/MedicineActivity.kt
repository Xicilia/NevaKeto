package com.example.nevaketo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.util.*

class MedicineActivity : AppCompatActivity() {

    private val mainLayout: LinearLayout by lazy {
        findViewById(R.id.layoutMedicine)
    }

    private val layoutParamsButton = LinearLayout.LayoutParams(500, LinearLayout.LayoutParams.MATCH_PARENT).also {
        it.setMargins(0, 36, 0, 36)
    }

    private val layoutParamsView = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT).also {
        it.setMargins(0, 24, 0, 24)
    }

    private val sharedPrefs: SPHelper by lazy {
        SPHelper(getSharedPreferences("USER", 0))
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
        setContentView(R.layout.activity_medicine)

        init()
    }

    private fun init() {
        mainLayout.removeAllViews()

        getMedicines()
        updateButton()
    }

    private fun getMedicines() {
        var index = 0
        while (sharedPrefs.contains("MEDICINE$index")) {
            createExistingMedicine(index)
            index++
        }
    }

    private fun createExistingMedicine(id: Int) {
        val medicineView = TextView(this).also { view ->
            view.text = sharedPrefs.getValue("MEDICINE$id")
            view.setTextColor(resources.getColor(R.color.white, theme))
            view.setBackgroundColor(resources.getColor(R.color.salemdark, theme))
            view.gravity = Gravity.CENTER
            view.textSize = 25.0f
            view.isClickable = true
            view.setOnClickListener {
                configureMedicine(id)
            }
        }

        mainLayout.addView(medicineView, layoutParamsView)
    }

    private fun createNewMedicine() {
        val currentMedicineIndex = sharedPrefs.getMaxValueFromList("MEDICINE") + 1

        sharedPrefs.putValue("MEDICINE$currentMedicineIndex", "Безымянные витамины")
        sharedPrefs.putValue("MEDICINEDESC$currentMedicineIndex", "")
        sharedPrefs.putValue("MEDICINEENABLED$currentMedicineIndex", false)

        configureMedicine(currentMedicineIndex)
    }

    private fun createNewButton() {
        val buttonView = Button(this).also { button ->
            button.setTextColor(resources.getColor(R.color.white))
            button.text = "Новые витамины"
            button.setBackgroundColor(resources.getColor(R.color.cerise, theme))
            button.setOnClickListener {
                createNewMedicine()
                updateButton()
            }
        }

        mainLayout.addView(buttonView, layoutParamsButton)
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

    private fun configureMedicine(id: Int) {
        val intent = Intent(this, MedicineConfigureActivity::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
    }
}