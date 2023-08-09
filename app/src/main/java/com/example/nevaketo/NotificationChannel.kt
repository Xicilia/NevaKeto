package com.example.nevaketo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

fun createChannel(context: Context,
                  id: String,
                  channelName: String = "StandartChannelName",
                  channelDescription: String = "StandartChannelDescription") {
    val importance = NotificationManager.IMPORTANCE_HIGH
    val channel = NotificationChannel(id, channelName, importance).apply {
        description = channelDescription
    }
    val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
    println("created channel $channelName")
}

