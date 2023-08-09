package com.example.nevaketo

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.getSystemService
import java.util.*

//TODO: combine this and eat thing

fun setExerciseBroadcastToAlarm(context: Context, id: Int) {

    val sharedPrefs = SPHelper(context.getSharedPreferences("USER", 0))

    val intent = Intent(context, ExerciseBroadcastReceiver::class.java)
    intent.putExtra("id", id)

    val pendingIntent = if (android.os.Build.VERSION.SDK_INT >= 31) {
        PendingIntent.getBroadcast(context,id + 200, intent, PendingIntent.FLAG_MUTABLE)
    } else {
        PendingIntent.getBroadcast(context,id + 200,intent,0)
    }

    val triggerTime = findTimeToTriggerEat( //will work for exercises also
        Calendar.getInstance(),
        sharedPrefs.getIntValue("EXERCISEHOUR$id"),
        sharedPrefs.getIntValue("EXERCISEMINUTE$id")
    )

    AlarmManagerCompat.setExact(
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager,
        AlarmManager.ELAPSED_REALTIME,
        SystemClock.elapsedRealtime() + (triggerTime * (AlarmManager.INTERVAL_FIFTEEN_MINUTES / 15)),
        pendingIntent
    )

    println("set exercise with id $id at $triggerTime trigger time")

}

fun canselExerciseBroadcastToAlarm(context: Context, id: Int) {
    val intent = Intent(context, ExerciseBroadcastReceiver::class.java)

    val pendingIntent = if (android.os.Build.VERSION.SDK_INT >= 31) {
        PendingIntent.getBroadcast(context,id,intent,PendingIntent.FLAG_MUTABLE)
    } else {
        PendingIntent.getBroadcast(context,id,intent,0)
    }

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(pendingIntent)

    println("cansel exercise with id $id")
}