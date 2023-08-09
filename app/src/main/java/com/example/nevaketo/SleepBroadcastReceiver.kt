package com.example.nevaketo

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.SystemClock
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class SleepBroadcastReceiver : BroadcastReceiver() {

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onReceive(context: Context, intent: Intent) {
        createChannel(context,"MainChannel")
        println("received intent")

        val notification = NotificationCompat.Builder(context,"MainChannel")
            .setContentTitle("Пора спать")
            .setContentText("Спокойной ночи!")
            .setSmallIcon(R.drawable.sleep_icon)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            //.setDefaults(NotificationCompat.DEFAULT_SOUND)
            .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(if (android.os.Build.VERSION.SDK_INT >= 31) {
                PendingIntent.getActivity(context,100,intent,PendingIntent.FLAG_MUTABLE)
            } else {
                PendingIntent.getActivity(context,100,intent,0)
            })

        with(NotificationManagerCompat.from(context)) {
            notify(1,notification.build())
        }

        val pendingIntent = if (android.os.Build.VERSION.SDK_INT >= 31) {
            PendingIntent.getBroadcast(context,100,intent,PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getBroadcast(context,100,intent,0)
        }

        AlarmManagerCompat.setExact(
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager,
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}