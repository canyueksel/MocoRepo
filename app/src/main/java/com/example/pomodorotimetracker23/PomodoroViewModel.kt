package com.example.pomodorotimetracker23

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

class PomodoroViewModel : ViewModel() {

    private val _timeLeft = mutableStateOf(60) // Zeit in Sekunden
    val timeLeft: State<Int> = _timeLeft

    private val _timerRunning = mutableStateOf(false)
    val timerRunning: State<Boolean> = _timerRunning

    val currentSession = PomodoroSession()

    fun startTimer(context: Context) {
        if (!_timerRunning.value) {
            _timerRunning.value = true
            viewModelScope.launch {
                runTimer(context)
            }
        }
    }

    private suspend fun runTimer(context: Context) {
        try {
            while (_timeLeft.value > 0 && _timerRunning.value) {
                delay(1000L)
                _timeLeft.value -= 1
            }
        } catch (e: Exception) {
            println("Fehler beim Timer: ${e.message}")
        } finally {
            _timerRunning.value = false
            // Timer ist abgelaufen, Benachrichtigung auslösen
            PomodoroUtility.showNotification(
                context,
                "Pomodoro Timer",
                "Die Arbeitszeit ist abgelaufen!"
            )
        }
    }

    fun stopTimer() {
        _timerRunning.value = false
    }

    fun resetTimer() {
        _timeLeft.value = currentSession.workDuration // Zurücksetzen auf 25 Minuten
        _timerRunning.value = false
    }

    fun initialize(context: Context) {
        // Möglicherweise benötigte Initialisierungen für das ViewModel
    }
}
