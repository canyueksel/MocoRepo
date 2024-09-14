package com.example.pomodorotimetracker23

data class PomodoroSession(
    val workDuration: Int = 25 * 60, // Arbeitszeit in Sekunden (25 Minuten)
    val breakDuration: Int = 5 * 60  // Pausenzeit in Sekunden (5 Minuten)
)
