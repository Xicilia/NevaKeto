package com.example.nevaketo

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.core.app.AlarmManagerCompat
import java.util.*

fun findNearestToCurrentTimeEat(sharedPrefs: SPHelper): Pair<Int,Long> {

    var currentEatId = 0

    val calendar = Calendar.getInstance()

    val currentTimeAsMinutes = (calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE)

    var minTimeDifference: Long = -1
    var minTimeDifferenceId = -1

    while (sharedPrefs.contains("EAT$currentEatId")) {

        val eatTimeAsMinutes = (sharedPrefs.getIntValue("EATHOUR$currentEatId") * 60) + sharedPrefs.getIntValue("EATMINUTE$currentEatId")

        var timeDiff: Long = (eatTimeAsMinutes - currentTimeAsMinutes).toLong()
        if (timeDiff < 0) {
            timeDiff += AlarmManager.INTERVAL_DAY
        }

        if (timeDiff in 1 until minTimeDifference || minTimeDifference == (-1).toLong()) {
            minTimeDifference = timeDiff
            minTimeDifferenceId = currentEatId
        }

        currentEatId++
    }

    return Pair(minTimeDifferenceId,minTimeDifference)
}

fun findTimeToTriggerEat(calendar: Calendar, hourToTrigger: Int, minutesToTrigger: Int): Long {

    val currentTime = (calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE)

    val chosenTime = (hourToTrigger * 60) + minutesToTrigger

    val timeDiff = (chosenTime - currentTime).toLong()

    return if (timeDiff > 0) {
        timeDiff
    } else {
        timeDiff + (AlarmManager.INTERVAL_DAY / 60000)
    }

}

fun setBroadcastToAlarm(context: Context,eatId: Int): Boolean {

    val sharedPrefs = SPHelper(context.
    getSharedPreferences("USER",0))

    val intent = Intent(context,EatBroadcastReceiver::class.java)
    intent.putExtra("EATID",eatId)

    val pendingIntent = if (android.os.Build.VERSION.SDK_INT >= 31) {
        PendingIntent.getBroadcast(context,eatId,intent,PendingIntent.FLAG_MUTABLE)
    } else {
        PendingIntent.getBroadcast(context,eatId,intent,0)
    }

    val triggerTime = findTimeToTriggerEat(
        Calendar.getInstance(),
        sharedPrefs.getIntValue("EATHOUR$eatId"),
        sharedPrefs.getIntValue("EATMINUTE$eatId")
    )

    AlarmManagerCompat.setExact(
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager,
        AlarmManager.ELAPSED_REALTIME,
        SystemClock.elapsedRealtime() + (triggerTime * (AlarmManager.INTERVAL_FIFTEEN_MINUTES / 15)),
        pendingIntent
    )

    println("succesfully set eat with id $eatId with delay $triggerTime minutes")
    return true
}

fun cancelBroadcastFromAlarm(context: Context, eatId: Int) {
    val intent = Intent(context,EatBroadcastReceiver::class.java)

    val pendingIntent = if (android.os.Build.VERSION.SDK_INT >= 31) {
        PendingIntent.getBroadcast(context,eatId,intent,PendingIntent.FLAG_MUTABLE)
    } else {
        PendingIntent.getBroadcast(context,eatId,intent,0)
    }

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(pendingIntent)

    println("succesfully cansel eat with id: $eatId")
}