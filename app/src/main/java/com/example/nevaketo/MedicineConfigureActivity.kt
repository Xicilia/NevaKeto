package com.example.nevaketo

import android.app.TimePickerDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import java.util.*

class MedicineConfigureActivity : AppCompatActivity() {

    private val sharedPrefs: SPHelper by lazy {
        SPHelper(getSharedPreferences("USER", 0))
    }

    private val id: Int by lazy {
        intent.getIntExtra("id", 0)
    }

    private val name: TextView by lazy {
        findViewById(R.id.medicineName)
    }

    private val description: TextView by lazy {
        findViewById(R.id.medicineDesc)
    }

    private val mainLayout: LinearLayout by lazy {
        findViewById(R.id.layoutMedicineTimes)
    }

    private val layoutParamsView = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT).also {
        it.setMargins(0, 24, 0, 24)
    }

    private var selectedTimeId = -1

    override fun onRestart() {
        super.onRestart()

        loadInfo()
    }

    override fun onResume() {
        super.onResume()

        loadInfo()
    }

    lateinit var listener: TimePickerDialog.OnTimeSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medicine_configure)

        createChannel(this, "MainChannel", "Main Channel")

        findViewById<Button>(R.id.medicineFullDelete).setOnClickListener {
            val dialogListener = DialogInterface.OnClickListener { _, selected ->
                when (selected) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        sharedPrefs.clearValueFromList("MEDICINE", id)
                        sharedPrefs.clearListedValue("MEDICINE${id}HOUR")
                        sharedPrefs.clearListedValue("MEDICINE${id}MINUTE")

                        Toast.makeText(this, "Успешно удалено!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
            val eatNameMessagePart = if (name.text.toString() == "") {
                "безымянные витамины"
            } else {
                "витамины \"${name.text}\""
            }
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder
                .setMessage("Вы действительно хотите удалить ${eatNameMessagePart}?")
                .setPositiveButton("Да", dialogListener)
                .setNegativeButton("Нет", dialogListener)
                .show()
        }

        findViewById<SwitchCompat>(R.id.medicineEnabled).also { switch ->
            switch.isChecked = sharedPrefs.getBooleanValue("MEDICINEENABLED$id")

            switch.setOnCheckedChangeListener { _, isChecked ->
                val message: String
                if (isChecked) {
                    setMedicineBroadcastToAlarm(this, id)
                    message = "Витамины включены"
                } else {
                    cancelMedicineBroadcastFromAlarm(this, id)
                    message = "Витамины выключены"
                }
                sharedPrefs.putValue("MEDICINEENABLED$id", isChecked)
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

       listener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            sharedPrefs.putValue("MEDICINE${id}HOUR$selectedTimeId", hour)
            sharedPrefs.putValue("MEDICINE${id}MINUTE$selectedTimeId", minute)

            if (findViewById<SwitchCompat>(R.id.medicineEnabled).isChecked) {
                setMedicineBroadcastToAlarm(this, id)
            }

            updateInfo()
            onRestart()
        }

        findViewById<Button>(R.id.medicineChange).setOnClickListener {
            if (selectedTimeId == -1) {
                Toast.makeText(this,
                    "Сначала выберите время, которое нужно изменить",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            TimePickerDialog(
                this,
                listener,
                sharedPrefs.getIntValue("MEDICINE${id}HOUR$selectedTimeId"),
                sharedPrefs.getIntValue("MEDICINE${id}MINUTE$selectedTimeId"),
                true
            ).show()
        }

        findViewById<Button>(R.id.medicineAdd).setOnClickListener {
            createNewMedicineTime()
        }

        findViewById<Button>(R.id.medicineDelete).setOnClickListener {
            if (selectedTimeId == -1) {
                Toast.makeText(this, "Сначала выберите время, чтобы его удалить", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            sharedPrefs.clearIntValueFromList("MEDICINE${id}HOUR", selectedTimeId)
            sharedPrefs.clearIntValueFromList("MEDICINE${id}MINUTE", selectedTimeId)

            onRestart()
        }

        loadInfo()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        updateInfo()
    }

    private fun loadInfo() {
        mainLayout.removeAllViews()

        val savedName = sharedPrefs.getValue("MEDICINE$id")
        name.text = if (savedName == "Безымянные витамины") {
            ""
        } else {
            savedName
        }

        val savedDescription = sharedPrefs.getValue("MEDICINEDESC$id")
        description.text = if (savedDescription == "NONE") {
            ""
        } else {
            savedDescription
        }

        var index = 0
        while(sharedPrefs.contains("MEDICINE${id}HOUR$index")) {
            createExistingMedicineTime(index)
            index++
        }

    }

    private fun createNewMedicineTime() {
        val calendar = Calendar.getInstance()

        val currentId = mainLayout.childCount
        val medicineView = TextView(this).also { view ->
            view.id = currentId + 500
            view.text = getTimeNormalized(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
            view.setTextColor(resources.getColor(R.color.white, theme))
            view.typeface = resources.getFont(R.font.pragmatica_common)
            view.setBackgroundColor(resources.getColor(R.color.salemdark, theme))
            view.gravity = Gravity.CENTER
            view.textSize = 25.0f
            view.isClickable = true
            view.setOnClickListener {
                setNewSelectedTime(view, currentId)
            }
        }

        mainLayout.addView(medicineView, layoutParamsView)
        setNewSelectedTime(medicineView, currentId)

        sharedPrefs.putValue("MEDICINE${id}HOUR$selectedTimeId", calendar.get(Calendar.HOUR_OF_DAY))
        sharedPrefs.putValue("MEDICINE${id}MINUTE$selectedTimeId", calendar.get(Calendar.MINUTE))
    }

    private fun createExistingMedicineTime(index: Int) {

        val medicineView = TextView(this).also { view ->
            view.id = index + 500
            view.text = getTimeNormalized(sharedPrefs.getIntValue("MEDICINE${id}HOUR$index"), sharedPrefs.getIntValue("MEDICINE${id}MINUTE$index"))
            view.setTextColor(resources.getColor(R.color.white, theme))
            view.typeface = resources.getFont(R.font.pragmatica_common)
            view.setBackgroundColor(resources.getColor(R.color.salemdark, theme))
            view.gravity = Gravity.CENTER
            view.textSize = 25.0f
            view.isClickable = true
            view.setOnClickListener {
                setNewSelectedTime(view, index)
            }
        }

        mainLayout.addView(medicineView, layoutParamsView)
    }

    private fun setNewSelectedTime(view: TextView, id: Int) {
        println("current id is $selectedTimeId, next id is $id")
        mainLayout.getChildAt(selectedTimeId).also {
            if (it is TextView){
                it.setTextColor(resources.getColor(R.color.white, theme))
            }
        }


        view.setTextColor(resources.getColor(R.color.cerise, theme))
        selectedTimeId = id
    }

    private fun updateInfo() {
        sharedPrefs.putValue("MEDICINE$id", if (name.text.isBlank()) "Безымянные витамины" else name.text.toString())
        sharedPrefs.putValue("MEDICINEDESC$id", description.text.toString())
    }
}