package com.example.pomodorotimetracker23

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

object PomodoroUtility {

    fun showNotification(context: Context, title: String, message: String) {
        val channelId = "pomodoro_channel_id"
        val notificationId = 1

        // Prüfe, ob die Berechtigung für Benachrichtigungen erteilt wurde
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Berechtigung nicht erteilt, Benachrichtigung wird nicht gesendet
                return
            }
        }

        // Notification Channel erstellen für Android 8.0 und höher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Pomodoro Timer"
            val descriptionText = "Pomodoro Timer Benachrichtigungen"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }
}
