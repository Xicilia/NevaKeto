package com.example.nevaketo

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.core.app.AlarmManagerCompat
import java.util.*

fun setMedicineBroadcastToAlarm(context: Context, id: Int) {
    val sharedPrefs = SPHelper(context.getSharedPreferences("USER",0))


    var index = 0
    while (sharedPrefs.contains("MEDICINE${id}HOUR$index")) {

        val intent = Intent(context,MedicineBroadcastReceiver::class.java)
        intent.putExtra("medicineId",id)
        intent.putExtra("medicineTime", index)

        val pendingIntent = if (android.os.Build.VERSION.SDK_INT >= 31) {
            PendingIntent.getBroadcast(context,(id + index) + 300,intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getBroadcast(context,(id + index) + 300,intent,0)
        }

        val triggerTime = findTimeToTriggerEat(
            Calendar.getInstance(),
            sharedPrefs.getIntValue("MEDICINE${id}HOUR$index"),
            sharedPrefs.getIntValue("MEDICINE${id}MINUTE$index")
        )

        AlarmManagerCompat.setExact(
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager,
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + (triggerTime * (AlarmManager.INTERVAL_FIFTEEN_MINUTES / 15)),
            pendingIntent
        )

        println("succesfully set medicine with id $id time: $index with delay $triggerTime minutes")

        index++
    }

}

fun cancelMedicineBroadcastFromAlarm(context: Context, id: Int) {

    val sharedPrefs = SPHelper(context.getSharedPreferences("USER",0))
    val intent = Intent(context,EatBroadcastReceiver::class.java)

    var index = 0
    while (sharedPrefs.contains("MEDICINE${id}HOUR$index")) {
        val pendingIntent = if (android.os.Build.VERSION.SDK_INT >= 31) {
            PendingIntent.getBroadcast(context,(id + index) + 300,intent,PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getBroadcast(context,(id + index) + 300,intent,0)
        }
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)

        println("succesfully cansel medicine with id: $id time: $index")
        index++
    }
}