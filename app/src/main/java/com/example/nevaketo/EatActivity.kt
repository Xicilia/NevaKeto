package com.example.nevaketo

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.allViews
import androidx.core.view.doOnLayout
import org.w3c.dom.Text
import java.util.*

class EatActivity : AppCompatActivity() {

    private val mainLayout: LinearLayout by lazy {
        findViewById(R.id.layoutMain)
    }

    private val sharedPrefs: SPHelper by lazy {
        SPHelper(getSharedPreferences("USER",0))
    }

    private val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT).also {
        it.setMargins(0, 24, 0, 24)
    }

    private val paramsButton = LinearLayout.LayoutParams(500, LinearLayout.LayoutParams.MATCH_PARENT).also {
        it.setMargins(0, 36, 0, 36)
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eat)
        init()
    }

    private fun init() {
        mainLayout.removeAllViews()

        getEatsFromSharedPrefs()
        updateEatButton()
    }

    private fun updateEatButton() {
        for (i in 0 until mainLayout.childCount) {
            val child = mainLayout.getChildAt(i)

            if (child is Button) {
                mainLayout.removeView(child)
            }
        }
        createNewEatButton()
    }

    private fun createNewEatButton() {
        val buttonView = Button(this).also { button ->
            button.setTextColor(resources.getColor(R.color.white, theme))
            button.text = resources.getText(R.string.newEat)
            button.setBackgroundColor(resources.getColor(R.color.cerise, theme))
            button.setOnClickListener {
                createNewEat()
                updateEatButton()
            }
        }
        mainLayout.addView(buttonView,paramsButton)
    }

    private fun createNewEat() {
        val currentEatIndex = sharedPrefs.getMaxValueFromList("EAT") + 1

        sharedPrefs.putValue("EATDESC$currentEatIndex",resources.getString(R.string.standartEatDesc)) // eat description
        sharedPrefs.putValue("EAT$currentEatIndex",resources.getString(R.string.unnamedEat)) // eat name

        val calendar = Calendar.getInstance()

        sharedPrefs.putValue("EATHOUR$currentEatIndex",calendar.get(Calendar.HOUR_OF_DAY)) // hours to trigger eat
        sharedPrefs.putValue("EATMINUTE$currentEatIndex",calendar.get(Calendar.MINUTE)) // minutes to trigger eat
        sharedPrefs.putValue("EATENABLED$currentEatIndex",0) // is eat enabled

        configureEat(currentEatIndex)
    }

    private fun createEatFromSharedPrefs(index: Int) {
        val eatView = TextView(this).also { text ->
            text.text = sharedPrefs.getValue("EAT$index")
            text.setTextColor(resources.getColor(R.color.white, theme))
            text.setBackgroundColor(resources.getColor(R.color.salemdark, theme))
            text.gravity = Gravity.CENTER
            text.setPadding(0,12,0,12)
            text.textSize = 25.0f
            text.isClickable = true
            text.setOnClickListener {
                configureEat(index)
            }
        }
        mainLayout.addView(eatView,params)
    }

    private fun getEatsFromSharedPrefs() {
        var index = 0
        while (sharedPrefs.contains("EAT$index")) {
            createEatFromSharedPrefs(index)
            index++
        }
    }

    private fun configureEat(index: Int) {
        val intent = Intent(this,EatConfigureActivity::class.java)
        intent.putExtra("EATKEY",index)
        startActivity(intent)
    }
}