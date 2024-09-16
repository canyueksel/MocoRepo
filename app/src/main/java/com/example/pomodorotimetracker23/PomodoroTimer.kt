package com.example.pomodorotimetracker23

import kotlinx.coroutines.*

class PomodoroTimer(
    private val workDuration: Long,
    private val shortBreakDuration: Long,
    private val longBreakDuration: Long,
    var onTick: (Long) -> Unit,
    var onSessionEnd: () -> Unit
) {
    private var job: Job? = null
    private val timerScope = CoroutineScope(Dispatchers.Main)

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
        job?.cancel() // Vorherige Sitzung beenden, wenn eine läuft
        val startTime = System.currentTimeMillis() // Zeitstempel des Starts
        job = timerScope.launch {
            var remainingTime = duration
            while (remainingTime > 0) {
                onTick(remainingTime)
                delay(1000)
                remainingTime = duration - (System.currentTimeMillis() - startTime)
            }
            onSessionEnd() // Sitzung ist beendet
        }
    }

    fun stop() {
        job?.cancel()
    }

    fun clear() {
        job?.cancel()
        timerScope.cancel() // CoroutineScope beenden, wenn der Timer nicht mehr gebraucht wird
    }
}
