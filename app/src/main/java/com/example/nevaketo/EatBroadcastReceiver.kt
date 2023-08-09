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
import java.util.*

class EatBroadcastReceiver: BroadcastReceiver() {
    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onReceive(context: Context, intent: Intent) {
        println("received eat intent")

        val sharedPrefs = SPHelper(context.getSharedPreferences("USER",0))
        var eatId = intent.getIntExtra("EATID",-1)
        if(eatId == -1) {
            eatId = getEatIdByTime(context)
        }
        val title = sharedPrefs.getValue("EAT$eatId")
        val description = sharedPrefs.getValue("EATDESC$eatId")

        val notification = NotificationCompat.Builder(context,"MainChannel")
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(R.drawable.eat_icon)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setDefaults(NotificationCompat.DEFAULT_SOUND)
            .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(
                if (android.os.Build.VERSION.SDK_INT >= 31) {
                    PendingIntent.getActivity(context,eatId,intent,PendingIntent.FLAG_MUTABLE)
                } else {
                    PendingIntent.getActivity(context,eatId,intent,0)
                }
            )

        with(NotificationManagerCompat.from(context)) {
            notify(1,notification.build())
            //println("notified")
        }


        val pendingIntent = if (android.os.Build.VERSION.SDK_INT >= 31) {
            PendingIntent.getBroadcast(context,eatId,intent,PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getBroadcast(context,eatId,intent,0)
        }

        AlarmManagerCompat.setExact(
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager,
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

        println("succesfully set eat with id $eatId with delay ${AlarmManager.INTERVAL_DAY} again!")
    }

    private fun getEatIdByTime(context: Context): Int {
        val calendar = Calendar.getInstance()

        val currentHours = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val sharedPrefs = SPHelper(context.getSharedPreferences("USER",0))

        var currentEatId = 0
        while (sharedPrefs.contains("EAT$currentEatId")) {
            if (
                currentHours == sharedPrefs.getIntValue("EATHOUR$currentEatId") &&
                currentMinute == sharedPrefs.getIntValue("EATMINUTE$currentEatId")
            ) {
                return currentEatId
            }
            currentEatId++
        }
        return -1
    }

}