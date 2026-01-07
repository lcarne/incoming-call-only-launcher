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

    private val _nightModeStartMinute = MutableStateFlow(prefs.getInt(KEY_NIGHT_START_MINUTE, 0))
    val nightModeStartMinute: StateFlow<Int> = _nightModeStartMinute.asStateFlow()

    private val _nightModeEndHour = MutableStateFlow(prefs.getInt(KEY_NIGHT_END, 7)) // Default 7h / 7AM
    val nightModeEndHour: StateFlow<Int> = _nightModeEndHour.asStateFlow()

    private val _nightModeEndMinute = MutableStateFlow(prefs.getInt(KEY_NIGHT_END_MINUTE, 0))
    val nightModeEndMinute: StateFlow<Int> = _nightModeEndMinute.asStateFlow()

    private val _isNightModeEnabled = MutableStateFlow(prefs.getBoolean(KEY_NIGHT_MODE_ENABLED, true))
    val isNightModeEnabled: StateFlow<Boolean> = _isNightModeEnabled.asStateFlow()

    private val _clockColor = MutableStateFlow(prefs.getInt(KEY_CLOCK_COLOR, 0)) // Default 0 (Use default blue/theme)
    val clockColor: StateFlow<Int> = _clockColor.asStateFlow()

    private val _allowAllCalls = MutableStateFlow(prefs.getBoolean(KEY_ALLOW_ALL_CALLS, false)) // Default False (Strict)
    val allowAllCalls: StateFlow<Boolean> = _allowAllCalls.asStateFlow()


    private val _isRingerEnabled = MutableStateFlow(prefs.getBoolean(KEY_RINGER_ENABLED, true))
    val isRingerEnabled: StateFlow<Boolean> = _isRingerEnabled.asStateFlow()

    private val _ringerVolume = MutableStateFlow(prefs.getInt(KEY_RINGER_VOLUME, 80)) // 0-100
    val ringerVolume: StateFlow<Int> = _ringerVolume.asStateFlow()



    private val _language = MutableStateFlow(prefs.getString(KEY_LANGUAGE, "fr") ?: "fr")
    val language: StateFlow<String> = _language.asStateFlow()

    private val _timeFormat = MutableStateFlow(prefs.getString(KEY_TIME_FORMAT, "24") ?: "24")
    val timeFormat: StateFlow<String> = _timeFormat.asStateFlow()

    private val _isDefaultSpeakerEnabled = MutableStateFlow(prefs.getBoolean(KEY_DEFAULT_SPEAKER_ENABLED, true)) // Default True (Speaker)
    val isDefaultSpeakerEnabled: StateFlow<Boolean> = _isDefaultSpeakerEnabled.asStateFlow()



    fun setAlwaysOnEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ALWAYS_ON, enabled).apply()
        _isAlwaysOnEnabled.value = enabled
    }

    fun setNightModeStartHour(hour: Int) {
        prefs.edit().putInt(KEY_NIGHT_START, hour).apply()
        _nightModeStartHour.value = hour
    }

    fun setNightModeStartMinute(minute: Int) {
        prefs.edit().putInt(KEY_NIGHT_START_MINUTE, minute).apply()
        _nightModeStartMinute.value = minute
    }

    fun setNightModeEndHour(hour: Int) {
        prefs.edit().putInt(KEY_NIGHT_END, hour).apply()
        _nightModeEndHour.value = hour
    }

    fun setNightModeEndMinute(minute: Int) {
        prefs.edit().putInt(KEY_NIGHT_END_MINUTE, minute).apply()
        _nightModeEndMinute.value = minute
    }

    fun setNightModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NIGHT_MODE_ENABLED, enabled).apply()
        _isNightModeEnabled.value = enabled
    }
    
    fun setClockColor(color: Int) {
        prefs.edit().putInt(KEY_CLOCK_COLOR, color).apply()
        _clockColor.value = color
    }

    fun setAllowAllCalls(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ALLOW_ALL_CALLS, enabled).apply()
        _allowAllCalls.value = enabled
    }


    fun setRingerEnabled(enabled: Boolean) {
        _isRingerEnabled.value = enabled
        prefs.edit().putBoolean(KEY_RINGER_ENABLED, enabled).apply()
    }

    fun setRingerVolume(volume: Int) {
        _ringerVolume.value = volume
        prefs.edit().putInt(KEY_RINGER_VOLUME, volume).apply()
    }



    fun setLanguage(lang: String) {
        prefs.edit().putString(KEY_LANGUAGE, lang).apply()
        _language.value = lang
    }

    fun setTimeFormat(format: String) {
        prefs.edit().putString(KEY_TIME_FORMAT, format).apply()
        _timeFormat.value = format
    }

    fun setDefaultSpeakerEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DEFAULT_SPEAKER_ENABLED, enabled).apply()
        _isDefaultSpeakerEnabled.value = enabled
    }



    companion object {
        private const val KEY_ALWAYS_ON = "always_on_enabled"
        private const val KEY_NIGHT_START = "night_mode_start"
        private const val KEY_NIGHT_START_MINUTE = "night_mode_start_minute"
        private const val KEY_NIGHT_END = "night_mode_end"
        private const val KEY_NIGHT_END_MINUTE = "night_mode_end_minute"
        private const val KEY_NIGHT_MODE_ENABLED = "night_mode_enabled"
        private const val KEY_CLOCK_COLOR = "clock_color"
        private const val KEY_ALLOW_ALL_CALLS = "allow_all_calls"
        private const val KEY_RINGER_ENABLED = "ringer_enabled"
        private const val KEY_RINGER_VOLUME = "ringer_volume"

        private const val KEY_LANGUAGE = "language"
        private const val KEY_TIME_FORMAT = "time_format"
        private const val KEY_DEFAULT_SPEAKER_ENABLED = "default_speaker_enabled"
    }
}
