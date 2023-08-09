package com.example.nevaketo

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.media.RingtoneManager
import android.os.SystemClock
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MedicineBroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        println("received medicine intent")

        val sharedPrefs = SPHelper(context.getSharedPreferences("USER", 0))
        val id = intent.getIntExtra("medicineId", -1)
        val time = intent.getIntExtra("medicineTime", -1)


        val title = sharedPrefs.getValue("MEDICINE$id")
        val description = sharedPrefs.getValue("MEDICINEDESC$id")

        val notification = NotificationCompat.Builder(context, "MainChannel")
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(R.drawable.vitamins_icon)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            //.setDefaults(NotificationCompat.DEFAULT_SOUND)
            .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(
                if (android.os.Build.VERSION.SDK_INT >= 31) {
                    PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_MUTABLE)
                } else {
                    PendingIntent.getActivity(context, id, intent, 0)
                }
            )

        with(NotificationManagerCompat.from(context)) {
            notify(1, notification.build())
        }

        val pendingIntent = if (android.os.Build.VERSION.SDK_INT >= 31) {
            PendingIntent.getBroadcast(context,(id + time) + 300,intent,PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getBroadcast(context,(id + time) + 300,intent,0)
        }

        AlarmManagerCompat.setExact(
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager,
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_DAY, //TODO: remake with trigger time
            pendingIntent
        )

        println("successfully set exercise with id $id with delay ${AlarmManager.INTERVAL_DAY} again!")
    }
}
