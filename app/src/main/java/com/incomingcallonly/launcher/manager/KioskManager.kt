package com.incomingcallonly.launcher.manager

import android.app.Activity
import android.app.NotificationManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.incomingcallonly.launcher.receivers.IncomingCallOnlyAdminReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KioskManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val _isKioskActive = MutableStateFlow(false)
    val isKioskActive: StateFlow<Boolean> = _isKioskActive.asStateFlow()

    fun hideSystemUI(activity: Activity) {
        val windowInsetsController =
            WindowCompat.getInsetsController(activity.window, activity.window.decorView)
        // Use immersive sticky mode for stable edge-to-edge display
        @Suppress("DEPRECATION")
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        // Enforce edge-to-edge mode to prevent black bars when returning from other apps
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)
        
        // Force transparent colors
        // Deprecated in API 35 (Vanilla Ice Cream) as edge-to-edge is enforced
        if (Build.VERSION.SDK_INT < 35) {
            @Suppress("DEPRECATION")
            activity.window.statusBarColor = android.graphics.Color.TRANSPARENT
            @Suppress("DEPRECATION")
            activity.window.navigationBarColor = android.graphics.Color.TRANSPARENT
        }
    }

    fun showSystemUI(activity: Activity) {
        val windowInsetsController =
            WindowCompat.getInsetsController(activity.window, activity.window.decorView)
        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        // Set edge-to-edge mode with visible system bars
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)
    }

    fun startKioskMode(activity: Activity) {
        val dpm = activity.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val adminComponent = ComponentName(activity, IncomingCallOnlyAdminReceiver::class.java)

        // Hide System UI
        hideSystemUI(activity)

        // Block Notifications
        val notificationManager = activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (notificationManager.isNotificationPolicyAccessGranted) {
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
            }
        }

        // Lock Task
        if (dpm.isDeviceOwnerApp(activity.packageName)) {
            if (!dpm.isLockTaskPermitted(activity.packageName)) {
                dpm.setLockTaskPackages(adminComponent, arrayOf(activity.packageName))
            }

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    dpm.setLockTaskFeatures(
                        adminComponent,
                        DevicePolicyManager.LOCK_TASK_FEATURE_NONE
                    )
                }
                dpm.setStatusBarDisabled(adminComponent, true)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }

        try {
            val am = activity.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
            if (am.lockTaskModeState == android.app.ActivityManager.LOCK_TASK_MODE_NONE) {
                activity.startLockTask()
            }
            // Start monitoring for state change because System might show a confirmation dialog
            // and we won't get a callback until the user accepts.
            startStateMonitoring(activity)
            
        } catch (e: Exception) {
            e.printStackTrace()
            syncKioskState(activity)
        }
    }

    private fun startStateMonitoring(activity: Activity) {
        // Poll for 15 seconds
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
            repeat(30) {
                syncKioskState(activity)
                if (_isKioskActive.value) {
                    // State detected as Active, stop polling
                    return@launch
                }
                delay(500)
            }
            // Final check
            syncKioskState(activity)
        }
    }

    fun stopKioskMode(activity: Activity) {
        try {
            activity.stopLockTask()
            _isKioskActive.value = false
            val dpm = activity.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            val adminComponent = ComponentName(activity, IncomingCallOnlyAdminReceiver::class.java)
            if (dpm.isDeviceOwnerApp(activity.packageName)) {
                dpm.setStatusBarDisabled(adminComponent, false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isKioskModeActive(activity: Activity): Boolean {
        return try {
            val am = activity.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
            am.lockTaskModeState != android.app.ActivityManager.LOCK_TASK_MODE_NONE
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Re-read the system lock task mode and update the internal StateFlow.
     * Useful to detect user-initiated unpin (e.g. system gesture) when
     * Activity.onLockTaskModeChanged override is not available.
     */
    fun syncKioskState(activity: Activity) {
        _isKioskActive.value = isKioskModeActive(activity)
    }
} 
