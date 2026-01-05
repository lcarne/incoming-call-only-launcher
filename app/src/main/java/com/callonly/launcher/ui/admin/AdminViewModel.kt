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
    private val callLogRepository: CallLogRepository
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
            repository.insertContact(Contact(name = name, phoneNumber = number, photoUri = photoUri))
        }
    }

    fun updateContact(contact: Contact) {
        viewModelScope.launch {
            repository.updateContact(contact)
        }
    }

    fun deleteContact(contact: Contact) {
        viewModelScope.launch {
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
