package com.callonly.launcher.data.repository

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("callonly_prefs", Context.MODE_PRIVATE)

    private val _isAlwaysOnEnabled = MutableStateFlow(prefs.getBoolean(KEY_ALWAYS_ON, true)) // Default True
    val isAlwaysOnEnabled: StateFlow<Boolean> = _isAlwaysOnEnabled.asStateFlow()

    private val _nightModeStartHour = MutableStateFlow(prefs.getInt(KEY_NIGHT_START, 22)) // Default 22h / 10PM
    val nightModeStartHour: StateFlow<Int> = _nightModeStartHour.asStateFlow()

    private val _nightModeEndHour = MutableStateFlow(prefs.getInt(KEY_NIGHT_END, 7)) // Default 7h / 7AM
    val nightModeEndHour: StateFlow<Int> = _nightModeEndHour.asStateFlow()

    private val _clockColor = MutableStateFlow(prefs.getInt(KEY_CLOCK_COLOR, 0)) // Default 0 (Use default blue/theme)
    val clockColor: StateFlow<Int> = _clockColor.asStateFlow()

    fun setAlwaysOnEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ALWAYS_ON, enabled).apply()
        _isAlwaysOnEnabled.value = enabled
    }

    fun setNightModeStartHour(hour: Int) {
        prefs.edit().putInt(KEY_NIGHT_START, hour).apply()
        _nightModeStartHour.value = hour
    }

    fun setNightModeEndHour(hour: Int) {
        prefs.edit().putInt(KEY_NIGHT_END, hour).apply()
        _nightModeEndHour.value = hour
    }
    
    fun setClockColor(color: Int) {
        prefs.edit().putInt(KEY_CLOCK_COLOR, color).apply()
        _clockColor.value = color
    }

    companion object {
        private const val KEY_ALWAYS_ON = "always_on_enabled"
        private const val KEY_NIGHT_START = "night_mode_start"
        private const val KEY_NIGHT_END = "night_mode_end"
        private const val KEY_CLOCK_COLOR = "clock_color"
    }
}
