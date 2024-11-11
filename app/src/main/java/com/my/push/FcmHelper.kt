package com.my.push

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException
import kotlin.math.acos


object FCMHelper {

    private const val TAG = "FCMHelper"
    private const val FCM_URL = "https://fcm.googleapis.com/v1/projects/fcmpush-79cf6/messages:send" // Replace with your Firebase project ID
    private val client = OkHttpClient()

    fun sendPushNotification(context: Activity, title: String, body: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                if (token != null) {
                    val messageJson = """
                        {
                          "message": {
                            "token": "$token",
                            "data": {
                              "title": "$title",
                              "body": "$body"
                            }
                          }
                        }
                    """.trimIndent()

                    CoroutineScope(Dispatchers.IO).launch {
                        sendNotificationToFCM(messageJson, context)
                    }
                } else {
                    Log.e(TAG, "FCM token is null!")
                }
            } else {
                Log.e(TAG, "Failed to get FCM token", task.exception)
            }
        }
    }

    private suspend fun sendNotificationToFCM(messageJson: String, context: Context) {

        val accessToken = getAccessToken(context)

        try {
            val bodyRequest = RequestBody.create("application/json".toMediaTypeOrNull(), messageJson)
            val request = Request.Builder()
                .url(FCM_URL)
                .post(bodyRequest)
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("Content-Type", "application/json")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    (context as Activity).runOnUiThread {
                        Toast.makeText(context, "FCM request failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        (context as Activity).runOnUiThread {
                            Toast.makeText(context, "FCM notification sent successfully!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        response.body?.let {
                            (context as Activity).runOnUiThread {
                                Toast.makeText(context, "FCM request failed with response: ${it.string()}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun getAccessToken(context: Context): String? = withContext(Dispatchers.IO) {
        try {
            val credentials = GoogleCredentials.fromStream(context.assets.open("file.json"))
                .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))

            credentials.refreshIfExpired() // Network call in the background
            credentials.accessToken?.tokenValue
        } catch (e: IOException) {
            Log.e(TAG, "Error reading service account file or generating token: ${e.message}")
            null
        }
    }
}



