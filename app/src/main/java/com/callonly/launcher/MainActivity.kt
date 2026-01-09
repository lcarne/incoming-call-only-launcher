package com.callonly.launcher

import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.media.AudioManager
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.callonly.launcher.data.repository.SettingsRepository
import com.callonly.launcher.manager.SimManager
import com.callonly.launcher.manager.SimStatus
import com.callonly.launcher.ui.call.IncomingCallScreen
import com.callonly.launcher.ui.call.IncomingCallViewModel
import com.callonly.launcher.ui.navigation.CallOnlyNavGraph
import com.callonly.launcher.ui.theme.CallOnlyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var simManager: SimManager

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private lateinit var audioManager: AudioManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        setupRingerManagement()

        // Block back button using modern API
        onBackPressedDispatcher.addCallback(
            this,
            object : androidx.activity.OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Do nothing - block back button for kiosk mode
                }
            })

        // Hide System UI for Kiosk feel
        hideSystemUI()

        setContent {
            CallOnlyTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Main App Content
                    CallOnlyNavGraph(
                        onUnpin = {
                            try {
                                stopLockTask()
                                // Re-enable Status Bar if we are Device Owner
                                val dpm =
                                    getSystemService(DEVICE_POLICY_SERVICE) as android.app.admin.DevicePolicyManager
                                val adminComponent = android.content.ComponentName(
                                    this@MainActivity,
                                    com.callonly.launcher.receivers.CallOnlyAdminReceiver::class.java
                                )
                                if (dpm.isDeviceOwnerApp(packageName)) {
                                    dpm.setStatusBarDisabled(adminComponent, false)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    )

                    // Incoming Call Overlay
                    val incomingCallViewModel: IncomingCallViewModel = hiltViewModel()
                    val uiState by incomingCallViewModel.incomingCallState.collectAsState()

                    if (uiState !is com.callonly.launcher.ui.call.IncomingCallUiState.Empty) {
                        IncomingCallScreen(
                            viewModel = incomingCallViewModel,
                            onCallRejected = { /* Managed by ViewModel */ },
                            onCallEnded = { /* Managed by ViewModel/State */ }
                        )
                    }

                    // SIM Lock Overlay
                    val simStatus by simManager.simStatus.collectAsState()
                    if (simStatus == SimStatus.LOCKED) {
                        SimLockOverlay(
                            onUnlockClick = {
                                try {
                                    stopLockTask()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        // Read saved language directly from prefs to apply before UI is created
        val prefs = newBase.getSharedPreferences("callonly_prefs", MODE_PRIVATE)
        val sysLocale = android.content.res.Resources.getSystem().configuration.locales[0]
        val defaultLang = if (sysLocale.language == "fr") "fr" else "en"
        val lang = prefs.getString("language", defaultLang) ?: defaultLang
        val locale = if (lang == "en") Locale.ENGLISH else Locale("fr")
        val context = updateLocale(newBase, locale)
        super.attachBaseContext(context)
    }

    private fun updateLocale(context: Context, locale: Locale): ContextWrapper {
        val config = Configuration(context.resources.configuration)
        Locale.setDefault(locale)
        config.setLocale(locale)
        val updated = context.createConfigurationContext(config)
        return ContextWrapper(updated)
    }

    @Composable
    private fun SimLockOverlay(onUnlockClick: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(com.callonly.launcher.ui.theme.Black)
                .padding(32.dp),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            androidx.compose.foundation.layout.Column(
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                    tint = com.callonly.launcher.ui.theme.ErrorRed
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = stringResource(R.string.sim_locked_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = com.callonly.launcher.ui.theme.White,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.sim_locked_message),
                    style = MaterialTheme.typography.bodyLarge,
                    color = com.callonly.launcher.ui.theme.LightGray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(64.dp))

                androidx.compose.material3.Button(
                    onClick = onUnlockClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = com.callonly.launcher.ui.theme.HighContrastButtonBg)
                ) {
                    Text(
                        stringResource(R.string.sim_unlock_button),
                        fontSize = 32.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
            }
        }
    }

    private fun setupRingerManagement() {
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        lifecycleScope.launch {
            combine(
                settingsRepository.isRingerEnabled,
                settingsRepository.ringerVolume
            ) { enabled, volumePercent ->
                Pair(enabled, volumePercent)
            }.collect { (enabled, volumePercent) ->
                try {
                    val hasDndAccess =
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            notificationManager.isNotificationPolicyAccessGranted
                        } else {
                            true
                        }

                    if (enabled) {
                        // Ringer is ON (White Button)
                        if (hasDndAccess) {
                            audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                        }
                        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)
                        val targetVolume = (volumePercent / 100f * maxVolume).toInt()
                        audioManager.setStreamVolume(AudioManager.STREAM_RING, targetVolume, 0)
                    } else {
                        // Ringer is OFF (Gray Button)
                        if (hasDndAccess) {
                            audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                        } else {
                            // Fallback: set volume to 0
                            audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0)
                        }
                    }
                } catch (e: SecurityException) {
                    e.printStackTrace()
                    // If we failed permission, try fallback volume 0 for silent
                    if (!enabled) {
                        try {
                            audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0)
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            // Block volume buttons as requested
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()

        // Block Notifications visibility if possible
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (notificationManager.isNotificationPolicyAccessGranted) {
                // Priority mode allows us to let calls through if configured in system, 
                // but user wants "only calls can ring". 
                // We'll trust our ringer management for the sound, 
                // and the overlay for the UI.
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
            }
        }

        // Kiosk Mode / Lock Task
        val dpm =
            getSystemService(DEVICE_POLICY_SERVICE) as android.app.admin.DevicePolicyManager
        val adminComponent = android.content.ComponentName(
            this,
            com.callonly.launcher.receivers.CallOnlyAdminReceiver::class.java
        )

        if (dpm.isDeviceOwnerApp(packageName)) {
            // Whitelist ourselves if we are owner
            if (!dpm.isLockTaskPermitted(packageName)) {
                dpm.setLockTaskPackages(adminComponent, arrayOf(packageName))
            }

            // STRICT KIOSK: Disable Status Bar and all Lock Task Features (Home, Notifications, etc.)
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    dpm.setLockTaskFeatures(
                        adminComponent,
                        android.app.admin.DevicePolicyManager.LOCK_TASK_FEATURE_NONE
                    )
                }
                dpm.setStatusBarDisabled(adminComponent, true)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }

        try {
            // Only start lock task if not already in lock task mode
            val am =
                getSystemService(ACTIVITY_SERVICE) as android.app.ActivityManager
            if (am.lockTaskModeState == android.app.ActivityManager.LOCK_TASK_MODE_NONE) {
                startLockTask()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun hideSystemUI() {
        val windowInsetsController =
            androidx.core.view.WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(androidx.core.view.WindowInsetsCompat.Type.systemBars())
    }

    private fun makeCall(phoneNumber: String) {
        val intent = android.content.Intent(android.content.Intent.ACTION_CALL).apply {
            data = android.net.Uri.parse("tel:$phoneNumber")
        }
        try {
            startActivity(intent)
        } catch (e: SecurityException) {
            // Should be handled by UI permission request before calling this
            e.printStackTrace()
        }
    }
}
