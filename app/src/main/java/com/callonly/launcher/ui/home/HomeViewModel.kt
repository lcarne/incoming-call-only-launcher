package com.callonly.launcher.ui.home

import android.telecom.TelecomManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callonly.launcher.data.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import android.content.Context
import android.app.role.RoleManager
import android.os.Build

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val settingsRepository: com.callonly.launcher.data.repository.SettingsRepository,
    private val screenManager: com.callonly.launcher.manager.ScreenManager
) : ViewModel() {

    val isAlwaysOnEnabled = settingsRepository.isAlwaysOnEnabled
    val nightModeStartHour = settingsRepository.nightModeStartHour
    val nightModeStartMinute = settingsRepository.nightModeStartMinute
    val nightModeEndHour = settingsRepository.nightModeEndHour
    val nightModeEndMinute = settingsRepository.nightModeEndMinute
    val clockColor = settingsRepository.clockColor
    val isRingerEnabled = settingsRepository.isRingerEnabled
    val timeFormat = settingsRepository.timeFormat
    val isNightModeEnabled = settingsRepository.isNightModeEnabled


    private val _isDefaultDialer = MutableStateFlow(true)
    val isDefaultDialer = _isDefaultDialer.asStateFlow()

    fun refreshDialerStatus(context: Context) {
        val isDefault = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService(Context.ROLE_SERVICE) as RoleManager
            roleManager.isRoleHeld(RoleManager.ROLE_DIALER)
        } else {
            val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            telecomManager.defaultDialerPackage == context.packageName
        }
        _isDefaultDialer.value = isDefault
    }

    fun setRingerEnabled(enabled: Boolean) {
        settingsRepository.setRingerEnabled(enabled)
    }

    fun wakeUpScreen() {
        screenManager.wakeUpScreen()
    }
}
