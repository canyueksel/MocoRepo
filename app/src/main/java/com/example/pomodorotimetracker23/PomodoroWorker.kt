package com.example.pomodorotimetracker23

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class PomodoroWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Daten erhalten, um festzustellen, ob es sich um die Pausenzeit handelt
        val isBreakTime = inputData.getBoolean("isBreakTime", false)

        val title = if (isBreakTime) {
            "Pausenphase beendet"
        } else {
            "Arbeitsphase beendet"
        }

        val message = if (isBreakTime) {
            "Deine Pause ist vorbei! Zeit, wieder zu arbeiten."
        } else {
            "Deine 25-minütige Arbeitszeit ist vorbei! Zeit für eine Pause."
        }

        // Benachrichtigung anzeigen
        PomodoroUtility.showNotification(
            context = applicationContext,
            title = title,
            message = message
        )

        return Result.success()
    }
}
