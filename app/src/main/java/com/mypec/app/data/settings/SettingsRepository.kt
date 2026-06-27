package com.mypec.app.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

enum class ThemeMode { SYSTEM, LIGHT, DARK }

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val dynamicColor: Boolean = false,
    val barWeightKg: Double = 20.0,
    val defaultRestSeconds: Int = 120,
    val reminderEnabled: Boolean = false,
    val reminderHour: Int = 18,
    val reminderMinute: Int = 0,
)

private val Context.dataStore by preferencesDataStore(name = "mypec_settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private object Keys {
        val THEME = stringPreferencesKey("theme_mode")
        val DYNAMIC = booleanPreferencesKey("dynamic_color")
        val BAR = doublePreferencesKey("bar_weight")
        val REST = intPreferencesKey("rest_seconds")
        val REMINDER_ON = booleanPreferencesKey("reminder_enabled")
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        val REMINDER_MIN = intPreferencesKey("reminder_minute")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { p ->
        AppSettings(
            themeMode = runCatching { ThemeMode.valueOf(p[Keys.THEME] ?: "DARK") }.getOrDefault(ThemeMode.DARK),
            dynamicColor = false,
            barWeightKg = p[Keys.BAR] ?: 20.0,
            defaultRestSeconds = p[Keys.REST] ?: 120,
            reminderEnabled = p[Keys.REMINDER_ON] ?: false,
            reminderHour = p[Keys.REMINDER_HOUR] ?: 18,
            reminderMinute = p[Keys.REMINDER_MIN] ?: 0,
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) =
        context.dataStore.edit { it[Keys.THEME] = mode.name }.let {}

    suspend fun setDynamicColor(enabled: Boolean) =
        context.dataStore.edit { it[Keys.DYNAMIC] = enabled }.let {}

    suspend fun setBarWeight(kg: Double) =
        context.dataStore.edit { it[Keys.BAR] = kg }.let {}

    suspend fun setRestSeconds(seconds: Int) =
        context.dataStore.edit { it[Keys.REST] = seconds }.let {}

    suspend fun setReminder(enabled: Boolean, hour: Int, minute: Int) =
        context.dataStore.edit {
            it[Keys.REMINDER_ON] = enabled
            it[Keys.REMINDER_HOUR] = hour
            it[Keys.REMINDER_MIN] = minute
        }.let {}
}
