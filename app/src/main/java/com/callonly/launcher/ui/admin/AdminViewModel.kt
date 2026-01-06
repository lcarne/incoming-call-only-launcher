package com.callonly.launcher.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callonly.launcher.data.model.Contact
import com.callonly.launcher.data.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.callonly.launcher.data.repository.CallLogRepository
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: ContactRepository,
    private val settingsRepository: com.callonly.launcher.data.repository.SettingsRepository,
    private val callLogRepository: CallLogRepository,
    private val imageStorageManager: com.callonly.launcher.util.ImageStorageManager
) : ViewModel() {

    val contacts = repository.getAllContacts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val isAlwaysOnEnabled = settingsRepository.isAlwaysOnEnabled
    val nightModeStartHour = settingsRepository.nightModeStartHour
    val nightModeEndHour = settingsRepository.nightModeEndHour
    val allowAllCalls = settingsRepository.allowAllCalls
    val answerButtonSize = settingsRepository.answerButtonSize
    val ringerVolume = settingsRepository.ringerVolume
    val isRingerEnabled = settingsRepository.isRingerEnabled
    val isVibrateEnabled = settingsRepository.isVibrateEnabled
    val language = settingsRepository.language
    val timeFormat = settingsRepository.timeFormat

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
            repository.insertContact(Contact(name = name, phoneNumber = number, photoUri = localUri))
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

    fun setAlwaysOnEnabled(enabled: Boolean) {
        settingsRepository.setAlwaysOnEnabled(enabled)
    }

    fun setNightModeStartHour(hour: Int) {
        settingsRepository.setNightModeStartHour(hour)
    }

    fun setNightModeEndHour(hour: Int) {
        settingsRepository.setNightModeEndHour(hour)
    }

    fun setAllowAllCalls(enabled: Boolean) {
        settingsRepository.setAllowAllCalls(enabled)
    }

    fun setAnswerButtonSize(size: Float) {
        settingsRepository.setAnswerButtonSize(size)
    }

    fun setRingerVolume(volume: Int) {
        settingsRepository.setRingerVolume(volume)
    }

    fun setRingerEnabled(enabled: Boolean) {
        settingsRepository.setRingerEnabled(enabled)
    }

    fun setVibrateEnabled(enabled: Boolean) {
        settingsRepository.setVibrateEnabled(enabled)
    }

    fun setLanguage(lang: String) {
        settingsRepository.setLanguage(lang)
    }

    fun setTimeFormat(format: String) {
        settingsRepository.setTimeFormat(format)
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
}
