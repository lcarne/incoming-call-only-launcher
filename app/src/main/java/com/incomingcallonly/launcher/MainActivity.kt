package com.incomingcallonly.launcher

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.incomingcallonly.launcher.manager.KioskManager
import com.incomingcallonly.launcher.manager.RingerManager
import com.incomingcallonly.launcher.manager.SimManager
import com.incomingcallonly.launcher.manager.SimStatus
import com.incomingcallonly.launcher.ui.call.IncomingCallScreen
import com.incomingcallonly.launcher.ui.call.IncomingCallUiState
import com.incomingcallonly.launcher.ui.call.IncomingCallViewModel
import com.incomingcallonly.launcher.ui.components.SimLockOverlay
import com.incomingcallonly.launcher.ui.navigation.IncomingCallOnlyNavGraph
import com.incomingcallonly.launcher.ui.theme.IncomingCallOnlyTheme
import com.incomingcallonly.launcher.ui.theme.SystemBarsColor
import androidx.compose.material3.MaterialTheme
import com.incomingcallonly.launcher.util.LocaleHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var simManager: SimManager

    @Inject
    lateinit var kioskManager: KioskManager

    @Inject
    lateinit var ringerManager: RingerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable modern edge-to-edge display (Android 15+)
        // enableEdgeToEdge() removed to keep status bar black and separate

        ringerManager.startObserving(lifecycleScope)

        // Block back button
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing - block back button for kiosk mode
            }
        })

        // Initial UI hide
        kioskManager.hideSystemUI(this)

        setContent {
            IncomingCallOnlyTheme {
                // Default System Bars Color: Set Status Bar to Black for Home/Lock screens
                // Admin/Settings screens will override this to White
                SystemBarsColor(
                    statusBarColor = androidx.compose.ui.graphics.Color.Black,
                    darkIcons = false
                )

                Box(modifier = Modifier.fillMaxSize()) {
                    // Main App Content
                    IncomingCallOnlyNavGraph(
                        onUnpin = {
                            kioskManager.stopKioskMode(this@MainActivity)
                        }
                    )

                    // Incoming Call Overlay
                    val incomingCallViewModel: IncomingCallViewModel = hiltViewModel()
                    val uiState by incomingCallViewModel.incomingCallState.collectAsState()

                    if (uiState !is IncomingCallUiState.Empty) {
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
                                kioskManager.stopKioskMode(this@MainActivity)
                            }
                        )
                    }
                }
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase))
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            // Block volume buttons
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onResume() {
        super.onResume()
        kioskManager.startKioskMode(this)
    }
}
