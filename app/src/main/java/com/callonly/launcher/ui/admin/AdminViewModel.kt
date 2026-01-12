package com.callonly.launcher.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callonly.launcher.data.model.Contact
import com.callonly.launcher.data.repository.CallLogRepository
import com.callonly.launcher.data.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: ContactRepository,
    private val settingsRepository: com.callonly.launcher.data.repository.SettingsRepository,
    private val callLogRepository: CallLogRepository,
    private val imageStorageManager: com.callonly.launcher.util.ImageStorageManager,
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context
) : ViewModel() {

    val contacts = repository.getAllContacts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

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

    val callLogs = callLogRepository.getAllCallLogs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated = _isAuthenticated.asStateFlow()

    private val _pinError = MutableStateFlow(false)
    val pinError = _pinError.asStateFlow()

    fun verifyPin(pin: String) {
        if (pin == "1234") { // Simple PIN for demo purposes
            _isAuthenticated.value = true
            _pinError.value = false
        } else {
            _pinError.value = true
        }
    }

    fun logout() {
        _isAuthenticated.value = false
    }

    fun addContact(name: String, number: String, photoUri: String?) {
        viewModelScope.launch {
            val localUri = photoUri?.let { uriStr ->
                imageStorageManager.saveImageLocally(android.net.Uri.parse(uriStr))
            }
            repository.insertContact(
                Contact(
                    name = name,
                    phoneNumber = number,
                    photoUri = localUri
                )
            )
        }
    }

    fun updateContact(contact: Contact) {
        viewModelScope.launch {
            // Check if photo changed
            val existingContact = repository.getContactById(contact.id)
            var newPhotoUri = contact.photoUri

            if (existingContact?.photoUri != contact.photoUri) {
                // Delete old local photo if it exists
                imageStorageManager.deleteImage(existingContact?.photoUri)

                // Save new one locally if it's not already in our storage
                newPhotoUri = contact.photoUri?.let { uriStr ->
                    if (!uriStr.startsWith("file://") || !uriStr.contains("contact_photos")) {
                        imageStorageManager.saveImageLocally(android.net.Uri.parse(uriStr))
                    } else {
                        uriStr
                    }
                }
            }

            repository.updateContact(contact.copy(photoUri = newPhotoUri))
        }
    }

    fun deleteContact(contact: Contact) {
        viewModelScope.launch {
            imageStorageManager.deleteImage(contact.photoUri)
            repository.deleteContact(contact)
        }
    }

    fun setScreenBehaviorPlugged(behavior: Int) {
        settingsRepository.setScreenBehaviorPlugged(behavior)
    }

    fun setScreenBehaviorBattery(behavior: Int) {
        settingsRepository.setScreenBehaviorBattery(behavior)
    }

    fun setNightModeStartHour(hour: Int) {
        settingsRepository.setNightModeStartHour(hour)
    }

    fun setNightModeStartMinute(minute: Int) {
        settingsRepository.setNightModeStartMinute(minute)
    }

    fun setNightModeEndHour(hour: Int) {
        settingsRepository.setNightModeEndHour(hour)
    }

    fun setNightModeEndMinute(minute: Int) {
        settingsRepository.setNightModeEndMinute(minute)
    }

    fun setNightModeEnabled(enabled: Boolean) {
        settingsRepository.setNightModeEnabled(enabled)
    }

    fun setAllowAllCalls(enabled: Boolean) {
        settingsRepository.setAllowAllCalls(enabled)
    }


    fun setRingerVolume(volume: Int) {
        settingsRepository.setRingerVolume(volume)
    }

    fun setRingerEnabled(enabled: Boolean) {
        settingsRepository.setRingerEnabled(enabled)
    }


    fun setLanguage(lang: String) {
        settingsRepository.setLanguage(lang)
    }

    fun setTimeFormat(format: String) {
        settingsRepository.setTimeFormat(format)
    }

    fun setDefaultSpeakerEnabled(enabled: Boolean) {
        settingsRepository.setDefaultSpeakerEnabled(enabled)
    }


    val clockColor = settingsRepository.clockColor
    fun setClockColor(color: Int) {
        settingsRepository.setClockColor(color)
    }

    fun clearCallHistory() {
        viewModelScope.launch {
            callLogRepository.clearHistory()
        }
    }

    private var currentRingtone: android.media.Ringtone? = null
    private var ringtoneJob: kotlinx.coroutines.Job? = null

    fun testRingtone() {
        // Stop existing ringtone and cancel the stop timer
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

                // Stop after 3 seconds
                kotlinx.coroutines.delay(3000)
                if (currentRingtone?.isPlaying == true) {
                    currentRingtone?.stop()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (currentRingtone?.isPlaying == true) {
            currentRingtone?.stop()
        }
    }

    fun exportContacts(uri: android.net.Uri) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val json = repository.exportContacts()
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(json.toByteArray())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun importContacts(uri: android.net.Uri) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val json = inputStream.bufferedReader().use { it.readText() }
                    repository.importContacts(json)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
