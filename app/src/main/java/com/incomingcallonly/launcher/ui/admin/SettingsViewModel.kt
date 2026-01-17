package com.incomingcallonly.launcher.ui.admin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.incomingcallonly.launcher.data.repository.CallLogRepository
import com.incomingcallonly.launcher.data.repository.ContactRepository
import com.incomingcallonly.launcher.data.repository.SettingsRepository
import com.incomingcallonly.launcher.util.ImageStorageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val callLogRepository: CallLogRepository,
    private val contactRepository: ContactRepository,
    private val imageStorageManager: ImageStorageManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val isNightModeEnabled = settingsRepository.isNightModeEnabled
    val screenBehaviorPlugged = settingsRepository.screenBehaviorPlugged
    val screenBehaviorBattery = settingsRepository.screenBehaviorBattery

    val nightModeStartHour = settingsRepository.nightModeStartHour
    val nightModeStartMinute = settingsRepository.nightModeStartMinute
    val nightModeEndHour = settingsRepository.nightModeEndHour
    val nightModeEndMinute = settingsRepository.nightModeEndMinute
    val allowAllCalls = settingsRepository.allowAllCalls
    val ringerVolume = settingsRepository.ringerVolume
    val isRingerEnabled = settingsRepository.isRingerEnabled
    val language = settingsRepository.language
    val timeFormat = settingsRepository.timeFormat
    val isDefaultSpeakerEnabled = settingsRepository.isDefaultSpeakerEnabled
    val clockColor = settingsRepository.clockColor

    // Call History Logic (Can be separated if needed, but fits in 'System' management)
     val callLogs = callLogRepository.getAllCallLogs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setScreenBehaviorPlugged(behavior: Int) = settingsRepository.setScreenBehaviorPlugged(behavior)
    fun setScreenBehaviorBattery(behavior: Int) = settingsRepository.setScreenBehaviorBattery(behavior)
    fun setNightModeStartHour(hour: Int) = settingsRepository.setNightModeStartHour(hour)
    fun setNightModeStartMinute(minute: Int) = settingsRepository.setNightModeStartMinute(minute)
    fun setNightModeEndHour(hour: Int) = settingsRepository.setNightModeEndHour(hour)
    fun setNightModeEndMinute(minute: Int) = settingsRepository.setNightModeEndMinute(minute)
    fun setNightModeEnabled(enabled: Boolean) = settingsRepository.setNightModeEnabled(enabled)
    fun setAllowAllCalls(enabled: Boolean) = settingsRepository.setAllowAllCalls(enabled)
    fun setRingerVolume(volume: Int) = settingsRepository.setRingerVolume(volume)
    fun setRingerEnabled(enabled: Boolean) = settingsRepository.setRingerEnabled(enabled)
    fun setLanguage(lang: String) = settingsRepository.setLanguage(lang)
    fun setTimeFormat(format: String) = settingsRepository.setTimeFormat(format)
    fun setDefaultSpeakerEnabled(enabled: Boolean) = settingsRepository.setDefaultSpeakerEnabled(enabled)
    fun setClockColor(color: Int) = settingsRepository.setClockColor(color)

    fun clearCallHistory() {
        viewModelScope.launch {
            callLogRepository.clearHistory()
        }
    }

    private var currentRingtone: android.media.Ringtone? = null
    private var ringtoneJob: kotlinx.coroutines.Job? = null

    fun testRingtone() {
        ringtoneJob?.cancel()
        if (currentRingtone?.isPlaying == true) {
            currentRingtone?.stop()
        }

        ringtoneJob = viewModelScope.launch {
            try {
                val uri = android.media.RingtoneManager.getActualDefaultRingtoneUri(
                    context,
                    android.media.RingtoneManager.TYPE_RINGTONE
                )
                currentRingtone = android.media.RingtoneManager.getRingtone(context, uri)
                currentRingtone?.play()
                kotlinx.coroutines.delay(3000)
                if (currentRingtone?.isPlaying == true) {
                    currentRingtone?.stop()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteAllData() {
        viewModelScope.launch {
            val currentContacts = contactRepository.getContactsList()
            currentContacts.forEach { contact ->
                imageStorageManager.deleteImage(contact.photoUri)
            }
            contactRepository.deleteAllContacts()
            callLogRepository.clearHistory()
        }
    }

    fun resetSettings() {
        settingsRepository.resetToDefaults()
    }

    override fun onCleared() {
        super.onCleared()
        if (currentRingtone?.isPlaying == true) {
            currentRingtone?.stop()
        }
    }
}
