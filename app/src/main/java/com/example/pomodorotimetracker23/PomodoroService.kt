package com.example.pomodorotimetracker23

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class PomodoroService : Service() {

    private val channelId = "pomodoro_foreground_channel_id"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, createNotification("Pomodoro Timer läuft"))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val isBreakTime = intent?.getBooleanExtra("isBreakTime", false) ?: false
        val title = if (isBreakTime) "Pausenphase beendet" else "Arbeitsphase beendet"
        val message = if (isBreakTime) "Deine Pause ist vorbei!" else "Deine Arbeitszeit ist vorbei!"

        // Aktualisiere die Benachrichtigung
        updateNotification(title, message)
        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Pomodoro Timer",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Pomodoro Timer Benachrichtigungen"
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(message: String): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Pomodoro Timer")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }

    private fun updateNotification(title: String, message: String) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
