package com.example.pomodorotimetracker23

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.content.pm.PackageManager
import android.widget.Toast

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted, continue with sending notifications
            sendTestNotification(this) // Beispiel: Eine Testbenachrichtigung senden
        } else {
            // Handle the case when permission is not granted
            // Zeige dem Benutzer eine Erklärung oder eine Nachricht an
            // und fordere die Berechtigung erneut an
            showPermissionDeniedMessage()
        }
    }

    private fun showPermissionDeniedMessage() {
        Toast.makeText(this, "Benachrichtigungsberechtigung erforderlich", Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PomodoroApp()
        }
        // Initiale Anfrage zur Berechtigung
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    @Composable
    fun PomodoroApp() {
        val context = LocalContext.current
        val pomodoroViewModel: PomodoroViewModel = viewModel()

        LaunchedEffect(Unit) {
            pomodoroViewModel.initialize(context) // ViewModel Initialisieren
        }

        PomodoroTimerScreen(pomodoroViewModel, context)
    }
}

@Composable
fun PomodoroTimerScreen(viewModel: PomodoroViewModel, context: Context) {
    val timeLeft by viewModel.timeLeft
    val timerRunning by viewModel.timerRunning

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = formatTime(timeLeft),
            fontSize = 48.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.startTimer(context)
            startForegroundService(context, isBreakTime = false) // Startet den Service
        }, enabled = !timerRunning) {
            Text(text = "Start")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.stopTimer()
            stopForegroundService(context) // Stoppt den Service
        }, enabled = timerRunning) {
            Text(text = "Stop")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.resetTimer() }) {
            Text(text = "Reset")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { sendTestNotification(context) }) {
            Text(text = "Send Test Notification")
        }
    }
}

fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secondsLeft = seconds % 60
    return String.format("%02d:%02d", minutes, secondsLeft)
}

fun startForegroundService(context: Context, isBreakTime: Boolean) {
    val intent = Intent(context, PomodoroService::class.java).apply {
        putExtra("isBreakTime", isBreakTime)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent) // Bei aktuellen Androidversionen ab Oreo
    } else {
        context.startService(intent) // Für Android Nougat und niedriger
    }
}

fun stopForegroundService(context: Context) {
    val intent = Intent(context, PomodoroService::class.java)
    context.stopService(intent)
}

private fun sendTestNotification(context: Context) {
    // Überprüfen, ob die Berechtigung zum Senden von Benachrichtigungen erteilt wurde
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Berechtigung nicht erteilt
            Toast.makeText(context, "Benachrichtigungsberechtigung nicht erteilt", Toast.LENGTH_LONG).show()
            return
        }
    }

    // Benachrichtigungskanal erstellen (nur für Android O und höher)
    val channelId = "test_channel_id"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channelName = "Test Channel"
        val channelDescription = "Channel for test notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    // Erstelle eine Benachrichtigung
    val notificationBuilder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("Test Notification")
        .setContentText("This is a test notification to check if it works.")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    // Benachrichtigung senden
    with(NotificationManagerCompat.from(context)) {
        notify(1, notificationBuilder.build())
    }
}
