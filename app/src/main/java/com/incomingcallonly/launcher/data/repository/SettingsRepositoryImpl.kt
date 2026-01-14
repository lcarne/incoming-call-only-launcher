package com.incomingcallonly.launcher.data.repository

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("incomingcallonly_prefs", Context.MODE_PRIVATE)

    private val _screenBehaviorPlugged =
        MutableStateFlow(prefs.getInt(KEY_SCREEN_BEHAVIOR_PLUGGED, SettingsRepository.SCREEN_BEHAVIOR_AWAKE))
    override val screenBehaviorPlugged: StateFlow<Int> = _screenBehaviorPlugged.asStateFlow()

    private val _screenBehaviorBattery =
        MutableStateFlow(prefs.getInt(KEY_SCREEN_BEHAVIOR_BATTERY, SettingsRepository.SCREEN_BEHAVIOR_OFF))
    override val screenBehaviorBattery: StateFlow<Int> = _screenBehaviorBattery.asStateFlow()

    private val _nightModeStartHour =
        MutableStateFlow(prefs.getInt(KEY_NIGHT_START, DEFAULT_NIGHT_START_HOUR)) // Default 22h / 10PM
    override val nightModeStartHour: StateFlow<Int> = _nightModeStartHour.asStateFlow()

    private val _nightModeStartMinute = MutableStateFlow(prefs.getInt(KEY_NIGHT_START_MINUTE, 0))
    override val nightModeStartMinute: StateFlow<Int> = _nightModeStartMinute.asStateFlow()

    private val _nightModeEndHour =
        MutableStateFlow(prefs.getInt(KEY_NIGHT_END, DEFAULT_NIGHT_END_HOUR)) // Default 7h / 7AM
    override val nightModeEndHour: StateFlow<Int> = _nightModeEndHour.asStateFlow()

    private val _nightModeEndMinute = MutableStateFlow(prefs.getInt(KEY_NIGHT_END_MINUTE, 0))
    override val nightModeEndMinute: StateFlow<Int> = _nightModeEndMinute.asStateFlow()

    private val _isNightModeEnabled =
        MutableStateFlow(prefs.getBoolean(KEY_NIGHT_MODE_ENABLED, true))
    override val isNightModeEnabled: StateFlow<Boolean> = _isNightModeEnabled.asStateFlow()

    private val _clockColor =
        MutableStateFlow(prefs.getInt(KEY_CLOCK_COLOR, 0)) // Default 0 (Use default blue/theme)
    override val clockColor: StateFlow<Int> = _clockColor.asStateFlow()

    private val _allowAllCalls =
        MutableStateFlow(prefs.getBoolean(KEY_ALLOW_ALL_CALLS, false)) // Default False (Strict)
    override val allowAllCalls: StateFlow<Boolean> = _allowAllCalls.asStateFlow()


    private val _isRingerEnabled = MutableStateFlow(prefs.getBoolean(KEY_RINGER_ENABLED, true))
    override val isRingerEnabled: StateFlow<Boolean> = _isRingerEnabled.asStateFlow()

    private val _ringerVolume = MutableStateFlow(prefs.getInt(KEY_RINGER_VOLUME, DEFAULT_RINGER_VOLUME)) // 0-100
    override val ringerVolume: StateFlow<Int> = _ringerVolume.asStateFlow()

    private val _preNightRingerEnabled = MutableStateFlow(prefs.getBoolean(KEY_PRE_NIGHT_RINGER_ENABLED, true))
    override val preNightRingerEnabled: StateFlow<Boolean> = _preNightRingerEnabled.asStateFlow()


    private val defaultLang = if (android.content.res.Resources.getSystem().configuration.locales[0].language == "fr") "fr" else "en"
    private val _language = MutableStateFlow(prefs.getString(KEY_LANGUAGE, defaultLang) ?: defaultLang)
    override val language: StateFlow<String> = _language.asStateFlow()

    private val _timeFormat = MutableStateFlow(prefs.getString(KEY_TIME_FORMAT, "24") ?: "24")
    override val timeFormat: StateFlow<String> = _timeFormat.asStateFlow()

    private val _isDefaultSpeakerEnabled = MutableStateFlow(
        prefs.getBoolean(
            KEY_DEFAULT_SPEAKER_ENABLED,
            true
        )
    ) // Default True (Speaker)
    override val isDefaultSpeakerEnabled: StateFlow<Boolean> = _isDefaultSpeakerEnabled.asStateFlow()

    private val _hasSeenOnboarding =
        MutableStateFlow(prefs.getBoolean(KEY_HAS_SEEN_ONBOARDING, false))
    override val hasSeenOnboarding: StateFlow<Boolean> = _hasSeenOnboarding.asStateFlow()

    private val _adminPin = MutableStateFlow(prefs.getString(KEY_ADMIN_PIN, DEFAULT_ADMIN_PIN) ?: DEFAULT_ADMIN_PIN)
    override val adminPin: StateFlow<String> = _adminPin.asStateFlow()


    override fun setScreenBehaviorPlugged(behavior: Int) {
        prefs.edit().putInt(KEY_SCREEN_BEHAVIOR_PLUGGED, behavior).apply()
        _screenBehaviorPlugged.value = behavior
    }

    override fun setScreenBehaviorBattery(behavior: Int) {
        prefs.edit().putInt(KEY_SCREEN_BEHAVIOR_BATTERY, behavior).apply()
        _screenBehaviorBattery.value = behavior
    }

    override fun setNightModeStartHour(hour: Int) {
        prefs.edit().putInt(KEY_NIGHT_START, hour).apply()
        _nightModeStartHour.value = hour
    }

    override fun setNightModeStartMinute(minute: Int) {
        prefs.edit().putInt(KEY_NIGHT_START_MINUTE, minute).apply()
        _nightModeStartMinute.value = minute
    }

    override fun setNightModeEndHour(hour: Int) {
        prefs.edit().putInt(KEY_NIGHT_END, hour).apply()
        _nightModeEndHour.value = hour
    }

    override fun setNightModeEndMinute(minute: Int) {
        prefs.edit().putInt(KEY_NIGHT_END_MINUTE, minute).apply()
        _nightModeEndMinute.value = minute
    }

    override fun setNightModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NIGHT_MODE_ENABLED, enabled).apply()
        _isNightModeEnabled.value = enabled
    }

    override fun setClockColor(color: Int) {
        prefs.edit().putInt(KEY_CLOCK_COLOR, color).apply()
        _clockColor.value = color
    }

    override fun setAllowAllCalls(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ALLOW_ALL_CALLS, enabled).apply()
        _allowAllCalls.value = enabled
    }


    override fun setRingerEnabled(enabled: Boolean) {
        _isRingerEnabled.value = enabled
        prefs.edit().putBoolean(KEY_RINGER_ENABLED, enabled).apply()
    }

    override fun setRingerVolume(volume: Int) {
        _ringerVolume.value = volume
        prefs.edit().putInt(KEY_RINGER_VOLUME, volume).apply()
    }

    override fun saveRingerStatePreNight(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_PRE_NIGHT_RINGER_ENABLED, enabled).apply()
        _preNightRingerEnabled.value = enabled
    }

    override fun restoreRingerStatePreNight() {
        val restoredValue = prefs.getBoolean(KEY_PRE_NIGHT_RINGER_ENABLED, true)
        setRingerEnabled(restoredValue)
    }


    override fun setLanguage(lang: String) {
        prefs.edit().putString(KEY_LANGUAGE, lang).apply()
        _language.value = lang
    }

    override fun setTimeFormat(format: String) {
        prefs.edit().putString(KEY_TIME_FORMAT, format).apply()
        _timeFormat.value = format
    }

    override fun setDefaultSpeakerEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DEFAULT_SPEAKER_ENABLED, enabled).apply()
        _isDefaultSpeakerEnabled.value = enabled
    }

    override fun setHasSeenOnboarding(hasSeen: Boolean) {
        prefs.edit().putBoolean(KEY_HAS_SEEN_ONBOARDING, hasSeen).apply()
        _hasSeenOnboarding.value = hasSeen
    }

    override fun setAdminPin(pin: String) {
        prefs.edit().putString(KEY_ADMIN_PIN, pin).apply()
        _adminPin.value = pin
    }


    companion object {
        private const val KEY_NIGHT_START = "night_mode_start"
        private const val KEY_NIGHT_START_MINUTE = "night_mode_start_minute"
        private const val KEY_NIGHT_END = "night_mode_end"
        private const val KEY_NIGHT_END_MINUTE = "night_mode_end_minute"
        private const val KEY_NIGHT_MODE_ENABLED = "night_mode_enabled"
        private const val KEY_CLOCK_COLOR = "clock_color"
        private const val KEY_ALLOW_ALL_CALLS = "allow_all_calls"
        private const val KEY_RINGER_ENABLED = "ringer_enabled"
        private const val KEY_RINGER_VOLUME = "ringer_volume"
        private const val KEY_SCREEN_BEHAVIOR_PLUGGED = "screen_behavior_plugged"
        private const val KEY_SCREEN_BEHAVIOR_BATTERY = "screen_behavior_battery"

        private const val KEY_PRE_NIGHT_RINGER_ENABLED = "pre_night_ringer_enabled"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_TIME_FORMAT = "time_format"
        private const val KEY_DEFAULT_SPEAKER_ENABLED = "default_speaker_enabled"
        private const val KEY_HAS_SEEN_ONBOARDING = "has_seen_onboarding"
        private const val KEY_ADMIN_PIN = "admin_pin"

        // Default Values
        private const val DEFAULT_NIGHT_START_HOUR = 22
        private const val DEFAULT_NIGHT_END_HOUR = 7
        private const val DEFAULT_RINGER_VOLUME = 80
        private const val DEFAULT_ADMIN_PIN = "1234"
    }
}
