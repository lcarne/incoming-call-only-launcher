package com.callonly.launcher.manager

import android.app.Activity
import android.app.NotificationManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.callonly.launcher.receivers.CallOnlyAdminReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KioskManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun hideSystemUI(activity: Activity) {
        val windowInsetsController =
            WindowCompat.getInsetsController(activity.window, activity.window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    fun startKioskMode(activity: Activity) {
        val dpm = activity.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val adminComponent = ComponentName(activity, CallOnlyAdminReceiver::class.java)

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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopKioskMode(activity: Activity) {
        try {
            activity.stopLockTask()
            val dpm = activity.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            val adminComponent = ComponentName(activity, CallOnlyAdminReceiver::class.java)
            if (dpm.isDeviceOwnerApp(activity.packageName)) {
                dpm.setStatusBarDisabled(adminComponent, false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
