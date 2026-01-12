package com.callonly.launcher.data.repository

import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val screenBehaviorPlugged: StateFlow<Int>
    val screenBehaviorBattery: StateFlow<Int>
    val nightModeStartHour: StateFlow<Int>
    val nightModeStartMinute: StateFlow<Int>
    val nightModeEndHour: StateFlow<Int>
    val nightModeEndMinute: StateFlow<Int>
    val isNightModeEnabled: StateFlow<Boolean>
    val clockColor: StateFlow<Int>
    val allowAllCalls: StateFlow<Boolean>
    val isRingerEnabled: StateFlow<Boolean>
    val ringerVolume: StateFlow<Int>
    val preNightRingerEnabled: StateFlow<Boolean>
    val language: StateFlow<String>
    val timeFormat: StateFlow<String>
    val isDefaultSpeakerEnabled: StateFlow<Boolean>
    val hasSeenOnboarding: StateFlow<Boolean>

    fun setScreenBehaviorPlugged(behavior: Int)
    fun setScreenBehaviorBattery(behavior: Int)
    fun setNightModeStartHour(hour: Int)
    fun setNightModeStartMinute(minute: Int)
    fun setNightModeEndHour(hour: Int)
    fun setNightModeEndMinute(minute: Int)
    fun setNightModeEnabled(enabled: Boolean)
    fun setClockColor(color: Int)
    fun setAllowAllCalls(enabled: Boolean)
    fun setRingerEnabled(enabled: Boolean)
    fun setRingerVolume(volume: Int)
    fun saveRingerStatePreNight(enabled: Boolean)
    fun restoreRingerStatePreNight()
    fun setLanguage(lang: String)
    fun setTimeFormat(format: String)
    fun setDefaultSpeakerEnabled(enabled: Boolean)
    fun setHasSeenOnboarding(hasSeen: Boolean)

    companion object {
        const val SCREEN_BEHAVIOR_OFF = 0
        const val SCREEN_BEHAVIOR_DIM = 1
        const val SCREEN_BEHAVIOR_AWAKE = 2
    }
}
