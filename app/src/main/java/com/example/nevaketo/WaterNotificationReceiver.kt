package com.example.nevaketo

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
//import android.os.Build
import android.os.SystemClock
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*

//import androidx.core.content.ContextCompat.getSystemService

class WaterNotificationReceiver : BroadcastReceiver() {

    /**
     * Notification Receiver.
     *
     * This class notificate user when he need to drink water.
     * */

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onReceive(context: Context, intent: Intent) {
        println("received water intent")

        println(intent.getStringExtra("TITLE"))

        val sharedPrefs = SPHelper(context.getSharedPreferences("USER",0))

        var isNotifying = true;
        val calendar = Calendar.getInstance()

        var currentIndex = 0;
        while (sharedPrefs.contains("WATERSKIPSTARTHOUR$currentIndex")) {
            val startHour = sharedPrefs.getIntValue("WATERSKIPSTARTHOUR$currentIndex")
            val startMinute = sharedPrefs.getIntValue("WATERSKIPSTARTMINUTE$currentIndex")

            val endHour = sharedPrefs.getIntValue("WATERSKIPENDHOUR$currentIndex")
            val endMinute = sharedPrefs.getIntValue("WATERSKIPENDMINUTE$currentIndex")

            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)

            if ((currentHour > startHour || (currentHour == startHour && currentMinute > startMinute))
                && (currentHour < endHour || (currentHour == endHour && currentMinute < endMinute))) {
                isNotifying = false
            }
            currentIndex++
        }

        if (isNotifying) {
            val notification = NotificationCompat.Builder(context,"MainChannel")
                .setContentTitle("Вода")
                .setContentText("Пора выпить воды, поторопитесь!")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(if (android.os.Build.VERSION.SDK_INT >= 31) {
                    PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_MUTABLE)
                } else {
                    PendingIntent.getActivity(context,0,intent,0)
                })

            with(NotificationManagerCompat.from(context)) {
                notify(1,notification.build())
                //println("notified")
            }
        }

        println("set to notify again")

        AlarmManagerCompat.setExact(
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager,
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime()+sharedPrefs.getLongValue("WATERNOTIFICATIONTIME"),
            if (android.os.Build.VERSION.SDK_INT >= 31) {
                PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_MUTABLE)
            } else {
                PendingIntent.getBroadcast(context, 0, intent, 0)
            }
        )

        sharedPrefs.putValue("WATERNOTIFICATIONLASTTIMEHOUR", calendar.get(Calendar.HOUR_OF_DAY))
        sharedPrefs.putValue("WATERNOTIFICATIONLASTTIMEMINUTE", calendar.get(Calendar.MINUTE))
    }
}