package com.mypec.app.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mypec.app.data.backup.BackupManager
import com.mypec.app.data.settings.AppSettings
import com.mypec.app.data.settings.SettingsRepository
import com.mypec.app.data.settings.ThemeMode
import com.mypec.app.notifications.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val reminderScheduler: ReminderScheduler,
    private val backupManager: BackupManager,
) : ViewModel() {

    val settings = settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    fun setTheme(mode: ThemeMode) = viewModelScope.launch { settingsRepository.setThemeMode(mode) }

    fun setDynamicColor(enabled: Boolean) =
        viewModelScope.launch { settingsRepository.setDynamicColor(enabled) }

    fun setBarWeight(kg: Double) = viewModelScope.launch { settingsRepository.setBarWeight(kg) }

    fun setRestSeconds(seconds: Int) =
        viewModelScope.launch { settingsRepository.setRestSeconds(seconds) }

    fun setReminder(enabled: Boolean, hour: Int, minute: Int) {
        viewModelScope.launch {
            settingsRepository.setReminder(enabled, hour, minute)
            if (enabled) reminderScheduler.schedule(hour, minute) else reminderScheduler.cancel()
        }
    }

    fun export(onReady: (String) -> Unit) {
        viewModelScope.launch { onReady(backupManager.exportJson()) }
    }
}
