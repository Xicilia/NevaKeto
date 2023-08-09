package com.example.nevaketo

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlin.math.abs
import kotlin.math.roundToInt

class AboutMeActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_me)
        val sharedPrefs = SPHelper(getSharedPreferences("USER",0))

        fun getInfoAboutMeasures(mainKey: String, currentMeasureKey: String): Pair<Int, Float>? {
            val lastDateIndex = sharedPrefs.getMaxValueFromList("${mainKey}DATE")
            if (lastDateIndex == 0) return null

            val lastDate = try {
                getDateObjectByString(sharedPrefs.getValue("${mainKey}DATE$lastDateIndex")!!)
            } catch (err: java.lang.IndexOutOfBoundsException) {
                return null
            }

            return Pair(
                getDayDifferenceBetweenDates(
                    lastDate,
                    getDateObjectByString(sharedPrefs.getValue("${mainKey}DATE0")!!)
                ),
                sharedPrefs.getValue("${mainKey}0")!!.toFloat() - sharedPrefs.getValue(currentMeasureKey)!!.toFloat()
            )
        }

        val weightInfo = getInfoAboutMeasures("WEIGHTHISTORY", "CURRENTWEIGHT")
        val waistInfo = getInfoAboutMeasures("WAISTHISTORY", "CURRENTWAIST")

        fun diffPrint(diff: Float, points:Int): String {
            return if (diff - diff.toInt() == 0.0f) {
                abs(diff).toInt().toString()
            } else {
                val str = String.format("%.${points}f", abs(diff))
                return str.replace(",",".")
            }
        }

        findViewById<TextView>(R.id.AboutNameField).text = sharedPrefs.getValue("NAME")
        findViewById<TextView>(R.id.AboutHeightField).text = "Рост: ${sharedPrefs.getIntValue("HEIGHT")}см"

        val currentWeight = sharedPrefs.getValue("CURRENTWEIGHT")
        findViewById<TextView>(R.id.AboutLastWeightField).text = "Текущее значение: ${if (currentWeight == "NONE") "-" else "${currentWeight}кг"}"

        val fullWeightText = if (weightInfo != null) {
            val keyword = if (weightInfo.second < 0.0) {
                "прибавлено"
            } else {
                "сброшено"
            }

            "За ${weightInfo.first} дней $keyword ${diffPrint(weightInfo.second, 1)}кг"
        } else {
            "Недостаточно информации для вычисления общего прогресса"
        }
        findViewById<TextView>(R.id.fullWeightLabel).text = fullWeightText

        val currentWaist = sharedPrefs.getValue("CURRENTWAIST")
        findViewById<TextView>(R.id.AboutWaistField).text = "Текущее значение: ${if (currentWaist == "NONE") "-" else "${currentWaist}см"}"
        val fullWaistText = if (waistInfo != null) {
            val keyword = if (waistInfo.second < 0.0) {
                "больше"
            } else {
                "меньше"
            }

            "За ${waistInfo.first} дней $keyword на ${diffPrint(waistInfo.second, 1)}см"
        } else {
            "Недостаточно информации для вычисления общего прогресса"
        }
        findViewById<TextView>(R.id.WaistFullField).text = fullWaistText


        findViewById<Button>(R.id.deleteProfile).setOnClickListener {
            val dialogListener = DialogInterface.OnClickListener { _, selected ->
                when (selected) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        sharedPrefs.clearAll()
                        val intent = Intent(this,MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }
                }
            }

            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder
                .setMessage("Вы действительно хотите удалить все данные?")
                .setPositiveButton("Да", dialogListener)
                .setNegativeButton("Нет", dialogListener)
                .show()
        }
    }
}

