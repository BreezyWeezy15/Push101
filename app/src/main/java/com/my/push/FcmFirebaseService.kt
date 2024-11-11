package com.my.push

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.my.push.BaseApplication.Companion.NOTIFICATION_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FcmFirebaseService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)


        Log.d("TAG", "Received message: $message")


        if (message.data.isNotEmpty()) {
            val title = message.data["title"]
            val body = message.data["body"]

            if (title != null && body != null) {
                showNotification(title, body)
            } else {
                Log.e("TAG", "Missing title or body in the data payload.")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(title: String, body: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = android.net.Uri.parse("https://www.google.com")
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationCompat = NotificationCompat.Builder(this, NOTIFICATION_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.bell)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(this).notify(1, notificationCompat)
    }
}