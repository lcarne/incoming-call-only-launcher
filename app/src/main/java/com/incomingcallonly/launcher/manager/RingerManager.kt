package com.incomingcallonly.launcher.manager

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import com.incomingcallonly.launcher.data.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RingerManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository
) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun startObserving(scope: CoroutineScope) {
        scope.launch {
            combine(
                settingsRepository.isRingerEnabled,
                settingsRepository.ringerVolume
            ) { enabled, volumePercent ->
                Pair(enabled, volumePercent)
            }.collect { (enabled, volumePercent) ->
                updateRinger(enabled, volumePercent)
            }
        }
    }

    private fun updateRinger(enabled: Boolean, volumePercent: Int) {
        try {
            val hasDndAccess = notificationManager.isNotificationPolicyAccessGranted

            if (enabled) {
                // Ringer is ON
                if (hasDndAccess) {
                    audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                }
                val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)
                val targetVolume = (volumePercent / 100f * maxVolume).toInt()
                audioManager.setStreamVolume(AudioManager.STREAM_RING, targetVolume, 0)
            } else {
                // Ringer is OFF
                if (hasDndAccess) {
                    audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                } else {
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback
            if (!enabled) {
                try {
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }
}
