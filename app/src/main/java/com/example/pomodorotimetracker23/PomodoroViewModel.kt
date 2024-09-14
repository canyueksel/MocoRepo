package com.example.pomodorotimetracker23

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

class PomodoroViewModel(private val context: Context) : ViewModel() {

    private val _timeLeft = mutableStateOf(1500) // Zeit in Sekunden
    val timeLeft: State<Int> = _timeLeft

    private val _timerRunning = mutableStateOf(false)
    val timerRunning: State<Boolean> = _timerRunning

    val currentSession = PomodoroSession()

    fun startTimer() {
        if (!_timerRunning.value) {
            _timerRunning.value = true
            viewModelScope.launch {
                runTimer() // Startet den Timer-Countdown
            }
        }
    }

    // Suspend function für den Timer-Countdown
    private suspend fun runTimer() {
        try {
            while (_timeLeft.value > 0 && _timerRunning.value) {
                delay(1000L) // Verzögerung von 1 Sekunde
                _timeLeft.value -= 1
            }
        } catch (e: Exception) {
            println("Error during timer: ${e.message}")
        } finally {
            _timerRunning.value = false // Timer stoppen, wenn fertig
            triggerBackgroundNotification(isBreakTime = false) // Benachrichtigung auslösen
        }
    }

    fun stopTimer() {
        _timerRunning.value = false
    }

    fun resetTimer() {
        _timeLeft.value = currentSession.workDuration // Zurücksetzen auf 25 Minuten
        _timerRunning.value = false
    }

    // Funktion zur Auslösung einer Benachrichtigung im Hintergrund
    private fun triggerBackgroundNotification(isBreakTime: Boolean) {
        val inputData = Data.Builder()
            .putBoolean("isBreakTime", isBreakTime)
            .build()

        val workRequest: WorkRequest = OneTimeWorkRequestBuilder<PomodoroWorker>()
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}
