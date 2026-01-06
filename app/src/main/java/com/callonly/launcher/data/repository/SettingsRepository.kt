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

    private val _allowAllCalls = MutableStateFlow(prefs.getBoolean(KEY_ALLOW_ALL_CALLS, false)) // Default False (Strict)
    val allowAllCalls: StateFlow<Boolean> = _allowAllCalls.asStateFlow()

    private val _answerButtonSize = MutableStateFlow(prefs.getFloat(KEY_ANSWER_BUTTON_SIZE, 120f)) // Default 120dp
    val answerButtonSize: StateFlow<Float> = _answerButtonSize.asStateFlow()

    private val _isRingerEnabled = MutableStateFlow(prefs.getBoolean(KEY_RINGER_ENABLED, true))
    val isRingerEnabled: StateFlow<Boolean> = _isRingerEnabled.asStateFlow()

    private val _ringerVolume = MutableStateFlow(prefs.getInt(KEY_RINGER_VOLUME, 80)) // 0-100
    val ringerVolume: StateFlow<Int> = _ringerVolume.asStateFlow()

    private val _isVibrateEnabled = MutableStateFlow(prefs.getBoolean(KEY_VIBRATE_ENABLED, true))
    val isVibrateEnabled: StateFlow<Boolean> = _isVibrateEnabled.asStateFlow()

    private val _language = MutableStateFlow(prefs.getString(KEY_LANGUAGE, "fr") ?: "fr")
    val language: StateFlow<String> = _language.asStateFlow()

    private val _timeFormat = MutableStateFlow(prefs.getString(KEY_TIME_FORMAT, "24") ?: "24")
    val timeFormat: StateFlow<String> = _timeFormat.asStateFlow()



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

    fun setAllowAllCalls(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ALLOW_ALL_CALLS, enabled).apply()
        _allowAllCalls.value = enabled
    }

    fun setAnswerButtonSize(size: Float) {
        _answerButtonSize.value = size
        prefs.edit().putFloat(KEY_ANSWER_BUTTON_SIZE, size).apply()
    }

    fun setRingerEnabled(enabled: Boolean) {
        _isRingerEnabled.value = enabled
        prefs.edit().putBoolean(KEY_RINGER_ENABLED, enabled).apply()
    }

    fun setRingerVolume(volume: Int) {
        _ringerVolume.value = volume
        prefs.edit().putInt(KEY_RINGER_VOLUME, volume).apply()
    }

    fun setVibrateEnabled(enabled: Boolean) {
        _isVibrateEnabled.value = enabled
        prefs.edit().putBoolean(KEY_VIBRATE_ENABLED, enabled).apply()
    }

    fun setLanguage(lang: String) {
        prefs.edit().putString(KEY_LANGUAGE, lang).apply()
        _language.value = lang
    }

    fun setTimeFormat(format: String) {
        prefs.edit().putString(KEY_TIME_FORMAT, format).apply()
        _timeFormat.value = format
    }



    companion object {
        private const val KEY_ALWAYS_ON = "always_on_enabled"
        private const val KEY_NIGHT_START = "night_mode_start"
        private const val KEY_NIGHT_END = "night_mode_end"
        private const val KEY_CLOCK_COLOR = "clock_color"
        private const val KEY_ALLOW_ALL_CALLS = "allow_all_calls"
        private const val KEY_ANSWER_BUTTON_SIZE = "answer_button_size"
        private const val KEY_RINGER_ENABLED = "ringer_enabled"
        private const val KEY_RINGER_VOLUME = "ringer_volume"
        private const val KEY_VIBRATE_ENABLED = "vibrate_enabled"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_TIME_FORMAT = "time_format"

    }
}
