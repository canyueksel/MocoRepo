package com.example.pomodorotimetracker23

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

class PomodoroViewModel : ViewModel() {

    private val _timeLeft = mutableStateOf(1500) // Zeit in Sekunden (25 Minuten)
    val timeLeft: State<Int> = _timeLeft

    private val _timerRunning = mutableStateOf(false)
    val timerRunning: State<Boolean> = _timerRunning

    private val currentSession = PomodoroSession()

    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences("pomodoro_prefs", Context.MODE_PRIVATE)
        restoreEndTime(context)
    }

    fun startTimer(context: Context) {
        if (!_timerRunning.value) {
            _timerRunning.value = true
            val endTime = System.currentTimeMillis() + (_timeLeft.value * 1000)
            saveEndTime(context, endTime)

            viewModelScope.launch {
                runTimer()
            }
        }
    }

    private suspend fun runTimer() {
        while (_timeLeft.value > 0 && _timerRunning.value) {
            delay(1000L)
            _timeLeft.value -= 1
        }
        _timerRunning.value = false
    }

    fun stopTimer(context: Context) {
        _timerRunning.value = false
        saveEndTime(context, 0L) // Endzeit zurücksetzen
    }

    fun resetTimer() {
        _timeLeft.value = currentSession.workDuration
        _timerRunning.value = false
    }

    fun saveEndTime(context: Context, endTime: Long) {
        sharedPreferences.edit().putLong("end_time", endTime).apply()
    }

    fun restoreEndTime(context: Context) {
        val endTime = sharedPreferences.getLong("end_time", 0L)
        if (endTime > 0) {
            val currentTime = System.currentTimeMillis()
            val remainingTime = (endTime - currentTime) / 1000
            if (remainingTime > 0) {
                _timeLeft.value = remainingTime.toInt()
                startTimer(context) // Den Timer neu starten, wenn noch Zeit übrig ist
            } else {
                _timeLeft.value = currentSession.workDuration
            }
        }
    }
}
