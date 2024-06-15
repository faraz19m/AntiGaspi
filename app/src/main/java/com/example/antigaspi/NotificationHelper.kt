package com.example.antigaspi

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.content.pm.PackageManager


class NotificationHelper(private val context: Context) {
    private val CHANNEL_ID = "food_expiration_channel"
    private val CHANNEL_NAME = "Food Expiration Notifications"
    private val CHANNEL_DESCRIPTION = "Notifications for food items about to expire"

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                description = CHANNEL_DESCRIPTION
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(title: String, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check for POST_NOTIFICATIONS permission on Android 13 and above
            if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, send the notification
                sendNotificationWithPermission(title, message)
            } else {
                // Permission is not granted, handle accordingly
                // TODO: Show a dialog to the user explaining why you need the permission and prompt them to grant it
            }
        } else {
            // For older versions, send the notification without checking for POST_NOTIFICATIONS permission
            sendNotificationWithPermission(title, message)
        }
    }

    private fun sendNotificationWithPermission(title: String, message: String) {
        try {
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_default_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), notification)
        } catch (e: SecurityException) {
            // Handle the SecurityException
            // TODO: Inform the user that the notification could not be sent and provide further instructions
        }
    }

}
