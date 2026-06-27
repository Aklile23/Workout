package com.mypec.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.mypec.app.domain.repository.ProgramRepository
import com.mypec.app.notifications.Notifications
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MyPecApplication : Application() {

    @Inject lateinit var programRepository: ProgramRepository

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        appScope.launch { programRepository.ensureSeeded() }
    }

    private fun createNotificationChannels() {
        val manager = getSystemService(NotificationManager::class.java)
        val reminders = NotificationChannel(
            Notifications.CHANNEL_REMINDERS,
            "Workout reminders",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply { description = "Reminders to train on schedule" }

        val timer = NotificationChannel(
            Notifications.CHANNEL_TIMER,
            "Rest timer",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply { description = "Rest timer finished" }

        manager.createNotificationChannel(reminders)
        manager.createNotificationChannel(timer)
    }
}
