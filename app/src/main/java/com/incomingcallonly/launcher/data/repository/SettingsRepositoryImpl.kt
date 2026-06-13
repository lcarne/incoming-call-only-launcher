package com.incomingcallonly.launcher.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.incomingcallonly.launcher.manager.NightModeScheduler
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
    private val prefs: SharedPreferences by lazy {
        val masterKey = androidx.security.crypto.MasterKey.Builder(context)
            .setKeyScheme(androidx.security.crypto.MasterKey.KeyScheme.AES256_GCM)
            .build()

        androidx.security.crypto.EncryptedSharedPreferences.create(
            context,
            "secret_incomingcallonly_prefs",
            masterKey,
            androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private val _screenBehaviorPlugged =
        MutableStateFlow(
            prefs.getInt(
                KEY_SCREEN_BEHAVIOR_PLUGGED,
                SettingsRepository.SCREEN_BEHAVIOR_AWAKE
            )
        )
    override val screenBehaviorPlugged: StateFlow<Int> = _screenBehaviorPlugged.asStateFlow()

    private val _screenBehaviorBattery =
        MutableStateFlow(
            prefs.getInt(
                KEY_SCREEN_BEHAVIOR_BATTERY,
                SettingsRepository.SCREEN_BEHAVIOR_OFF
            )
        )
    override val screenBehaviorBattery: StateFlow<Int> = _screenBehaviorBattery.asStateFlow()

    private val _nightModeStartHour =
        MutableStateFlow(
            prefs.getInt(
                KEY_NIGHT_START,
                DEFAULT_NIGHT_START_HOUR
            )
        ) // Default 22h / 10PM
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

    private val _ringerVolume =
        MutableStateFlow(prefs.getInt(KEY_RINGER_VOLUME, DEFAULT_RINGER_VOLUME)) // 0-100
    override val ringerVolume: StateFlow<Int> = _ringerVolume.asStateFlow()

    private val _preNightRingerEnabled =
        MutableStateFlow(prefs.getBoolean(KEY_PRE_NIGHT_RINGER_ENABLED, true))
    override val preNightRingerEnabled: StateFlow<Boolean> = _preNightRingerEnabled.asStateFlow()


    private val defaultLang = android.content.res.Resources.getSystem().configuration.locales[0].language.let { lang ->
        when (lang) {
            "fr" -> "fr"
            "es" -> "es"
            else -> "en"
        }
    }
    private val _language =
        MutableStateFlow(prefs.getString(KEY_LANGUAGE, defaultLang) ?: defaultLang)
    override val language: StateFlow<String> = _language.asStateFlow()

    private val _timeFormat = MutableStateFlow(prefs.getString(KEY_TIME_FORMAT, "24") ?: "24")
    override val timeFormat: StateFlow<String> = _timeFormat.asStateFlow()

    private val _isDefaultSpeakerEnabled = MutableStateFlow(
        prefs.getBoolean(
            KEY_DEFAULT_SPEAKER_ENABLED,
            true
        )
    ) // Default True (Speaker)
    override val isDefaultSpeakerEnabled: StateFlow<Boolean> =
        _isDefaultSpeakerEnabled.asStateFlow()

    private val _hasSeenOnboarding =
        MutableStateFlow(prefs.getBoolean(KEY_HAS_SEEN_ONBOARDING, false))
    override val hasSeenOnboarding: StateFlow<Boolean> = _hasSeenOnboarding.asStateFlow()

    private val _adminPin =
        MutableStateFlow(prefs.getString(KEY_ADMIN_PIN, DEFAULT_ADMIN_PIN) ?: DEFAULT_ADMIN_PIN)
    override val adminPin: StateFlow<String> = _adminPin.asStateFlow()

    private val _lastSelectedCountryCode =
        MutableStateFlow(
            prefs.getString(KEY_LAST_COUNTRY_CODE, DEFAULT_COUNTRY_CODE) ?: DEFAULT_COUNTRY_CODE
        )
    override val lastSelectedCountryCode: StateFlow<String> = _lastSelectedCountryCode.asStateFlow()

    private val _failedAttempts =
        MutableStateFlow(prefs.getInt(KEY_FAILED_ATTEMPTS, 0))
    override val failedAttempts: StateFlow<Int> = _failedAttempts.asStateFlow()

    private val _lockoutEndTime =
        MutableStateFlow(prefs.getLong(KEY_LOCKOUT_END_TIME, 0L))
    override val lockoutEndTime: StateFlow<Long> = _lockoutEndTime.asStateFlow()


    override fun setScreenBehaviorPlugged(behavior: Int) {
        prefs.edit { putInt(KEY_SCREEN_BEHAVIOR_PLUGGED, behavior) }
        _screenBehaviorPlugged.value = behavior
    }

    override fun setScreenBehaviorBattery(behavior: Int) {
        prefs.edit { putInt(KEY_SCREEN_BEHAVIOR_BATTERY, behavior) }
        _screenBehaviorBattery.value = behavior
    }

    override fun setNightModeStartHour(hour: Int) {
        prefs.edit { putInt(KEY_NIGHT_START, hour) }
        _nightModeStartHour.value = hour
        NightModeScheduler.scheduleNightModeEnd(context)
    }

    override fun setNightModeStartMinute(minute: Int) {
        prefs.edit { putInt(KEY_NIGHT_START_MINUTE, minute) }
        _nightModeStartMinute.value = minute
        NightModeScheduler.scheduleNightModeEnd(context)
    }

    override fun setNightModeEndHour(hour: Int) {
        prefs.edit { putInt(KEY_NIGHT_END, hour) }
        _nightModeEndHour.value = hour
        NightModeScheduler.scheduleNightModeEnd(context)
    }

    override fun setNightModeEndMinute(minute: Int) {
        prefs.edit { putInt(KEY_NIGHT_END_MINUTE, minute) }
        _nightModeEndMinute.value = minute
        NightModeScheduler.scheduleNightModeEnd(context)
    }

    override fun setNightModeEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_NIGHT_MODE_ENABLED, enabled) }
        _isNightModeEnabled.value = enabled
        NightModeScheduler.scheduleNightModeEnd(context)
    }

    override fun setClockColor(color: Int) {
        prefs.edit { putInt(KEY_CLOCK_COLOR, color) }
        _clockColor.value = color
    }

    override fun setAllowAllCalls(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_ALLOW_ALL_CALLS, enabled) }
        _allowAllCalls.value = enabled
    }


    override fun setRingerEnabled(enabled: Boolean) {
        _isRingerEnabled.value = enabled
        prefs.edit { putBoolean(KEY_RINGER_ENABLED, enabled) }
    }

    override fun setRingerVolume(volume: Int) {
        _ringerVolume.value = volume
        prefs.edit { putInt(KEY_RINGER_VOLUME, volume) }
    }

    private val _ringerMode =
        MutableStateFlow(prefs.getInt(KEY_RINGER_MODE, SettingsRepository.RINGER_MODE_SWITCHABLE))
    override val ringerMode: StateFlow<Int> = _ringerMode.asStateFlow()

    override fun setRingerMode(mode: Int) {
        prefs.edit { putInt(KEY_RINGER_MODE, mode) }
        _ringerMode.value = mode

        // Enforce immediate effect
        if (!_isNightModeEnabled.value) {
            // If night mode is disabled, we just respect the forced setting
            when (mode) {
                SettingsRepository.RINGER_MODE_FORCED_ON -> setRingerEnabled(true)
                SettingsRepository.RINGER_MODE_FORCED_OFF -> setRingerEnabled(false)
            }
        } else {
            // Night mode is enabled, check if we are currently in night time
            if (isCurrentlyNight()) {
                // It is night. Ringer should be OFF.
                // Even if Forced ON, Night Mode takes precedence (User Rule: night mode is more important)
                setRingerEnabled(false)
            } else {
                // It is NOT night. Apply forced settings.
                when (mode) {
                    SettingsRepository.RINGER_MODE_FORCED_ON -> setRingerEnabled(true)
                    SettingsRepository.RINGER_MODE_FORCED_OFF -> setRingerEnabled(false)
                }
            }
        }
    }

    private fun isCurrentlyNight(): Boolean {
        val startHour = _nightModeStartHour.value
        val startMinute = _nightModeStartMinute.value
        val endHour = _nightModeEndHour.value
        val endMinute = _nightModeEndMinute.value

        val now = java.util.Calendar.getInstance()
        val currentHour = now.get(java.util.Calendar.HOUR_OF_DAY)
        val currentMinute = now.get(java.util.Calendar.MINUTE)

        val currentTimeVal = currentHour * 60 + currentMinute
        val startTimeVal = startHour * 60 + startMinute
        val endTimeVal = endHour * 60 + endMinute

        return if (startTimeVal > endTimeVal) {
            // Crosses midnight (e.g. 22:00 to 07:00)
            currentTimeVal >= startTimeVal || currentTimeVal < endTimeVal
        } else {
            // Same day (e.g. 01:00 to 05:00)
            currentTimeVal in startTimeVal until endTimeVal
        }
    }

    override fun saveRingerStatePreNight(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_PRE_NIGHT_RINGER_ENABLED, enabled) }
        _preNightRingerEnabled.value = enabled
    }

    override fun restoreRingerStatePreNight() {
        val restoredValue = prefs.getBoolean(KEY_PRE_NIGHT_RINGER_ENABLED, true)
        val mode = _ringerMode.value
        if (mode == SettingsRepository.RINGER_MODE_FORCED_ON) {
            setRingerEnabled(true)
        } else if (mode == SettingsRepository.RINGER_MODE_FORCED_OFF) {
            setRingerEnabled(false)
        } else {
            setRingerEnabled(restoredValue)
        }
    }


    override fun setLanguage(lang: String) {
        prefs.edit { putString(KEY_LANGUAGE, lang) }
        _language.value = lang
    }

    override fun setTimeFormat(format: String) {
        prefs.edit { putString(KEY_TIME_FORMAT, format) }
        _timeFormat.value = format
    }

    override fun setDefaultSpeakerEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_DEFAULT_SPEAKER_ENABLED, enabled) }
        _isDefaultSpeakerEnabled.value = enabled
    }

    override fun setHasSeenOnboarding(hasSeen: Boolean) {
        prefs.edit { putBoolean(KEY_HAS_SEEN_ONBOARDING, hasSeen) }
        _hasSeenOnboarding.value = hasSeen
    }

    override fun setAdminPin(pin: String) {
        prefs.edit { putString(KEY_ADMIN_PIN, pin) }
        _adminPin.value = pin
    }

    override fun setLastSelectedCountryCode(code: String) {
        prefs.edit { putString(KEY_LAST_COUNTRY_CODE, code) }
        _lastSelectedCountryCode.value = code
    }

    override fun incrementFailedAttempts() {
        val current = _failedAttempts.value
        val newCount = current + 1
        prefs.edit { putInt(KEY_FAILED_ATTEMPTS, newCount) }
        _failedAttempts.value = newCount
    }

    override fun resetFailedAttempts() {
        prefs.edit { putInt(KEY_FAILED_ATTEMPTS, 0) }
        _failedAttempts.value = 0
    }

    override fun setLockoutEndTime(timestamp: Long) {
        prefs.edit { putLong(KEY_LOCKOUT_END_TIME, timestamp) }
        _lockoutEndTime.value = timestamp
    }

    override fun resetToDefaults() {
        setScreenBehaviorPlugged(SettingsRepository.SCREEN_BEHAVIOR_AWAKE)
        setScreenBehaviorBattery(SettingsRepository.SCREEN_BEHAVIOR_OFF)
        setNightModeStartHour(DEFAULT_NIGHT_START_HOUR)
        setNightModeStartMinute(0)
        setNightModeEndHour(DEFAULT_NIGHT_END_HOUR)
        setNightModeEndMinute(0)
        setNightModeEnabled(true)
        setClockColor(0)
        setAllowAllCalls(false)
        setRingerEnabled(true)
        setRingerMode(SettingsRepository.RINGER_MODE_SWITCHABLE)
        setRingerVolume(DEFAULT_RINGER_VOLUME)
        setDefaultSpeakerEnabled(true)
        setLanguage(defaultLang)
        setTimeFormat("24")
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
        private const val KEY_LAST_COUNTRY_CODE = "last_country_code"
        private const val KEY_RINGER_MODE = "ringer_mode"
        private const val KEY_FAILED_ATTEMPTS = "failed_attempts"
        private const val KEY_LOCKOUT_END_TIME = "lockout_end_time"

        // Default Values
        private const val DEFAULT_NIGHT_START_HOUR = 22
        private const val DEFAULT_NIGHT_END_HOUR = 7
        private const val DEFAULT_RINGER_VOLUME = 80
        private const val DEFAULT_ADMIN_PIN = "1234"
        private const val DEFAULT_COUNTRY_CODE = "+33"
    }
}
