package com.callonly.launcher.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callonly.launcher.data.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: ContactRepository,
    private val settingsRepository: com.callonly.launcher.data.repository.SettingsRepository
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
    val clockColor = settingsRepository.clockColor
}
