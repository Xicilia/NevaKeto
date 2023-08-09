package com.example.nevaketo

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity

class SPHelper(SharedPrefArg: SharedPreferences) : AppCompatActivity() {

    private val sharedPrefs = SharedPrefArg
    private val editor = sharedPrefs.edit()

    fun putValue(key: String, value: String?) {
        editor.putString(key,value)
        editor.apply()
    }
    fun putValue(key: String, value: Int) {
        editor.putInt(key,value)
        editor.apply()
    }
    fun putValue(key:String, value: Float) {
        editor.putFloat(key,value)
        editor.apply()
    }
    fun putValue(key:String, value: Boolean) {
        editor.putBoolean(key,value)
        editor.apply()

    }
    fun putValue(key: String,value: Long) {
        editor.putLong(key,value)
        editor.apply()
    }

    fun getValue(key: String) : String? {
        return sharedPrefs.getString(key,"NONE")
    }
    fun getIntValue(key: String, standardValue: Int = 0) : Int {
        return sharedPrefs.getInt(key,standardValue)
    }
    fun getLongValue(key:String) : Long {
        return sharedPrefs.getLong(key,0.toLong())
    }
    fun getBooleanValue(key: String, standardValue: Boolean = false): Boolean {
        return sharedPrefs.getBoolean(key,standardValue)
    }

    fun contains(key: String) : Boolean {
        return sharedPrefs.contains(key)
    }

    fun clearAll() {
        editor.clear()
        editor.apply()
    }

    fun clearValue(key: String) {
        editor.remove(key)
        editor.apply()
    }

    fun clearListedValue(key: String) {

        var lastIndex = 0
        var lastKey = key + lastIndex.toString()
        while(sharedPrefs.contains(lastKey)) {

            editor.remove(lastKey)
            lastIndex++
            lastKey = key + lastIndex.toString()

        }
        editor.apply()
    }

    fun getMaxValueFromList(key:String): Int {
        var currentIndex = 0
        while (contains("${key}${currentIndex}")) {
            currentIndex++
        }
        return currentIndex - 1
    }

    fun clearIntValueFromList(key: String, indexToDelete: Int) {

        val listCapacity = getMaxValueFromList(key)
        //println("list capacity: ${listCapacity}")

        clearValue("${key}${indexToDelete}")

        if (indexToDelete == listCapacity) return

        for (i in indexToDelete until listCapacity) {
            //println("putting value from ${i + 1} to $i")
            putValue("${key}$i",getIntValue("${key}${i + 1}"))
            clearValue("${key}${i + 1}")
        }

    }

    fun clearValueFromList(key: String, indexToDelete:Int) {

        val listCapacity = getMaxValueFromList(key)
        //println("list capacity: ${listCapacity}")

        clearValue("${key}${indexToDelete}")

        if (indexToDelete == listCapacity) return

        for (i in indexToDelete until listCapacity) {
            //println("putting value from ${i + 1} to $i")
            putValue("${key}$i",getValue("${key}${i + 1}"))
            clearValue("${key}${i + 1}")
        }

    }
}