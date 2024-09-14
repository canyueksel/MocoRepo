package com.example.pomodorotimetracker23

import kotlinx.coroutines.*
import androidx.compose.runtime.*

class PomodoroTimer(
    private val workDuration: Long,
    private val shortBreakDuration: Long,
    private val longBreakDuration: Long,
    var onTick: (Long) -> Unit,
    var onSessionEnd: () -> Unit
) {
    private var job: Job? = null

    fun startWorkSession() {
        startSession(workDuration)
    }

    fun startShortBreak() {
        startSession(shortBreakDuration)
    }

    fun startLongBreak() {
        startSession(longBreakDuration)
    }

    private fun startSession(duration: Long) {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            for (time in duration downTo 0 step 1000) {
                onTick(time)
                delay(1000)
            }
            onSessionEnd()
        }
    }

    fun stop() {
        job?.cancel()
    }
}