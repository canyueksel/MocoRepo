package com.example.pomodorotimetracker23

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted, continue with posting notifications
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PomodoroApp()
        }
    }

    @Composable
    fun PomodoroApp() {
        val context = LocalContext.current
        val pomodoroViewModel: PomodoroViewModel = viewModel()

        LaunchedEffect(Unit) {
            pomodoroViewModel.initialize(context) // Initialisiere das ViewModel
        }

        PomodoroTimerScreen(pomodoroViewModel)
    }
}

@Composable
fun PomodoroTimerScreen(viewModel: PomodoroViewModel) {
    val timeLeft by viewModel.timeLeft
    val timerRunning by viewModel.timerRunning
    val context = LocalContext.current

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

        Button(onClick = { viewModel.startTimer(context) }, enabled = !timerRunning) {
            Text(text = "Start")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.stopTimer(context) }, enabled = timerRunning) {
            Text(text = "Stop")
        }

        Spacer(modifier = Modifier.height(16.dp))

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
