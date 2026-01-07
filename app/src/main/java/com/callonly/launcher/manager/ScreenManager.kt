package com.callonly.launcher.manager

import android.content.Context
import android.os.PowerManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScreenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    private var wakeLock: PowerManager.WakeLock? = null
    
    /**
     * Wakes up the screen from deep sleep using a WakeLock.
     * The wake lock is automatically released after 3 seconds.
     */
    @Suppress("DEPRECATION")
    fun wakeUpScreen() {
        try {
            // Release any existing wake lock first
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }
            
            // Create a new wake lock with ACQUIRE_CAUSES_WAKEUP flag to turn on the screen
            wakeLock = powerManager.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "CallOnly::ScreenWakeLock"
            )
            
            // Acquire the wake lock
            wakeLock?.acquire(3000L) // 3 seconds timeout
            
            // Release after 3 seconds in case the timeout doesn't work
            CoroutineScope(Dispatchers.Main).launch {
                delay(3000L)
                wakeLock?.let {
                    if (it.isHeld) {
                        it.release()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
