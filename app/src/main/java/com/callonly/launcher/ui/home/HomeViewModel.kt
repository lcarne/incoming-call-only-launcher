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

    val screenBehaviorPlugged = settingsRepository.screenBehaviorPlugged
    val screenBehaviorBattery = settingsRepository.screenBehaviorBattery
    val nightModeStartHour = settingsRepository.nightModeStartHour
    val nightModeStartMinute = settingsRepository.nightModeStartMinute
    val nightModeEndHour = settingsRepository.nightModeEndHour
    val nightModeEndMinute = settingsRepository.nightModeEndMinute
    val clockColor = settingsRepository.clockColor
    val isRingerEnabled = settingsRepository.isRingerEnabled
    val timeFormat = settingsRepository.timeFormat
    val isNightModeEnabled = settingsRepository.isNightModeEnabled
    val hasSeenOnboarding = settingsRepository.hasSeenOnboarding


    private val _isDefaultDialer = MutableStateFlow(true)
    val isDefaultDialer = _isDefaultDialer.asStateFlow()

    private val _isDefaultLauncher = MutableStateFlow(true)
    val isDefaultLauncher = _isDefaultLauncher.asStateFlow()

    fun refreshDefaultAppStatus(context: Context) {
        // Check Dialer
        val isDefaultDialer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService(Context.ROLE_SERVICE) as RoleManager
            roleManager.isRoleHeld(RoleManager.ROLE_DIALER)
        } else {
            val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            telecomManager.defaultDialerPackage == context.packageName
        }
        _isDefaultDialer.value = isDefaultDialer

        // Check Launcher
        val isDefaultLauncher = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService(Context.ROLE_SERVICE) as RoleManager
            roleManager.isRoleHeld(RoleManager.ROLE_HOME)
        } else {
            val intent = android.content.Intent(android.content.Intent.ACTION_MAIN)
            intent.addCategory(android.content.Intent.CATEGORY_HOME)
            val resolveInfo = context.packageManager.resolveActivity(intent, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY)
            val currentLauncherPackage = resolveInfo?.activityInfo?.packageName
            currentLauncherPackage == context.packageName
        }
        _isDefaultLauncher.value = isDefaultLauncher
    }

    fun setRingerEnabled(enabled: Boolean) {
        settingsRepository.setRingerEnabled(enabled)
    }

    fun wakeUpScreen() {
        screenManager.wakeUpScreen()
    }

    fun markOnboardingSeen() {
        settingsRepository.setHasSeenOnboarding(true)
    }
}
