package com.incomingcallonly.launcher.ui.home

import android.app.role.RoleManager
import android.content.Context
import android.os.Build
import android.telecom.TelecomManager
import androidx.lifecycle.ViewModel
import com.incomingcallonly.launcher.data.repository.SettingsRepository
import com.incomingcallonly.launcher.manager.ScreenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val screenManager: ScreenManager,
    private val kioskManager: com.incomingcallonly.launcher.manager.KioskManager
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

    private val _isPinned = MutableStateFlow(false)
    val isPinned = _isPinned.asStateFlow()

    init {
        // Keep ViewModel state in sync with KioskManager's flow
        viewModelScope.launch {
            kioskManager.isKioskActive.collect { active ->
                _isPinned.value = active
            }
        }
    }

    fun refreshPinStatus() {
        // Read the KioskManager's StateFlow to avoid race conditions with Activity lock task state
        _isPinned.value = kioskManager.isKioskActive.value
    }


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
            val resolveInfo = context.packageManager.resolveActivity(
                intent,
                android.content.pm.PackageManager.MATCH_DEFAULT_ONLY
            )
            val currentLauncherPackage = resolveInfo?.activityInfo?.packageName
            currentLauncherPackage == context.packageName
        }
        _isDefaultLauncher.value = isDefaultLauncher
    }

    fun setRingerEnabled(enabled: Boolean) {
        settingsRepository.setRingerEnabled(enabled)
    }

    fun saveRingerStatePreNight(enabled: Boolean) {
        settingsRepository.saveRingerStatePreNight(enabled)
    }

    fun restoreRingerStatePreNight() {
        settingsRepository.restoreRingerStatePreNight()
    }

    fun wakeUpScreen() {
        screenManager.wakeUpScreen()
    }

    fun markOnboardingSeen() {
        settingsRepository.setHasSeenOnboarding(true)
    }
}
