package com.example.pomodorotimetracker23

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {

    // Verwenden des ViewModelFactory, um den Context zu übergeben
    private val viewModel: PomodoroViewModel by viewModels { PomodoroViewModelFactory(this) }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted, continue with posting notifications
            startPomodoroTimer()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PomodoroTimerScreen()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                        PackageManager.PERMISSION_GRANTED -> startPomodoroTimer()
                else -> requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            startPomodoroTimer()
        }
    }

    private fun startPomodoroTimer() {
        // Using ViewModel's logic to control the timer, notifications will be handled by the ViewModel
    }
}

@Composable
fun PomodoroTimerScreen(viewModel: PomodoroViewModel = viewModel()) {
    val context = LocalContext.current // Kontext aus der Composable holen
    val timeLeft by viewModel.timeLeft
    val timerRunning by viewModel.timerRunning

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        // Timer Text
        Text(
            text = formatTime(timeLeft),
            fontSize = 48.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Start Button
        Button(onClick = { viewModel.startTimer() }, enabled = !timerRunning) {
            Text(text = "Start")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stop Button
        Button(onClick = { viewModel.stopTimer() }, enabled = timerRunning) {
            Text(text = "Stop")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reset Button
        Button(onClick = { viewModel.resetTimer() }) {
            Text(text = "Reset")
        }
    }
}

fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secondsLeft = seconds % 60
    return String.format("%02d:%02d", minutes, secondsLeft)
}

