package com.callonly.launcher

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.callonly.launcher.manager.KioskManager
import com.callonly.launcher.manager.RingerManager
import com.callonly.launcher.manager.SimManager
import com.callonly.launcher.manager.SimStatus
import com.callonly.launcher.ui.call.IncomingCallScreen
import com.callonly.launcher.ui.call.IncomingCallUiState
import com.callonly.launcher.ui.call.IncomingCallViewModel
import com.callonly.launcher.ui.components.SimLockOverlay
import com.callonly.launcher.ui.navigation.CallOnlyNavGraph
import com.callonly.launcher.ui.theme.CallOnlyTheme
import com.callonly.launcher.util.LocaleHelper
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
            CallOnlyTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Main App Content
                    CallOnlyNavGraph(
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
