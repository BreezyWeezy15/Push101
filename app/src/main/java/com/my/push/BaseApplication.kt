package com.my.push

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log

class BaseApplication : Application() {

    companion object {
        const val NOTIFICATION_NAME = "fcm"
        const val NOTIFICATION_ID = "id"

    }

    @SuppressLint("HardwareIds")
    override fun onCreate() {
        super.onCreate()
        createNotification()
    }

    private fun createNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                NOTIFICATION_ID, NOTIFICATION_NAME,NotificationManager.IMPORTANCE_LOW
            )
            notificationChannel.enableVibration(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                notificationChannel.setAllowBubbles(true)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}