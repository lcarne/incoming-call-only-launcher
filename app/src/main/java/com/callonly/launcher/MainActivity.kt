package com.callonly.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import com.callonly.launcher.ui.navigation.CallOnlyNavGraph
import com.callonly.launcher.ui.theme.CallOnlyTheme
import com.callonly.launcher.ui.call.IncomingCallScreen
import com.callonly.launcher.ui.call.IncomingCallViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Box
import androidx.hilt.navigation.compose.hiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Hide System UI for Kiosk feel
        hideSystemUI()

        setContent {
            CallOnlyTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Main App Content
                    CallOnlyNavGraph(
                        onCall = { contact ->
                            makeCall(contact.phoneNumber)
                        },
                        onUnpin = {
                            try {
                                stopLockTask()
                                // Re-enable Status Bar if we are Device Owner
                                val dpm = getSystemService(android.content.Context.DEVICE_POLICY_SERVICE) as android.app.admin.DevicePolicyManager
                                val adminComponent = android.content.ComponentName(this@MainActivity, com.callonly.launcher.receivers.CallOnlyAdminReceiver::class.java)
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
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
        
        // Kiosk Mode / Lock Task
        val dpm = getSystemService(android.content.Context.DEVICE_POLICY_SERVICE) as android.app.admin.DevicePolicyManager
        val adminComponent = android.content.ComponentName(this, com.callonly.launcher.receivers.CallOnlyAdminReceiver::class.java)
        
        if (dpm.isDeviceOwnerApp(packageName)) {
            // Whitelist ourselves if we are owner
            if (!dpm.isLockTaskPermitted(packageName)) {
                dpm.setLockTaskPackages(adminComponent, arrayOf(packageName))
            }
            
            // STRICT KIOSK: Disable Status Bar and all Lock Task Features (Home, Notifications, etc.)
            try {
                dpm.setLockTaskFeatures(adminComponent, android.app.admin.DevicePolicyManager.LOCK_TASK_FEATURE_NONE)
                dpm.setStatusBarDisabled(adminComponent, true)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
        
        try {
             // Only start lock task if not already in lock task mode
             val am = getSystemService(android.content.Context.ACTIVITY_SERVICE) as android.app.ActivityManager
             if (am.lockTaskModeState == android.app.ActivityManager.LOCK_TASK_MODE_NONE) {
                 startLockTask()
             }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Deprecated("Deprecated in Java", ReplaceWith("Unit"))
    override fun onBackPressed() {
        // block back button
        // super.onBackPressed() 
    }

    private fun hideSystemUI() {
        val windowInsetsController = androidx.core.view.WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
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
