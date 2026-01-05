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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Hide System UI for Kiosk feel
        hideSystemUI()

        setContent {
            CallOnlyTheme {
                CallOnlyNavGraph(
                    onCall = { contact ->
                        makeCall(contact.phoneNumber)
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
        // Attempt to start Lock Task mode (Screen Pinning)
        // Note: For true Kiosk (no user confirm), app must be Device Owner. 
        // Otherwise this asks user to pin.
        try {
             // startLockTask() // Uncomment to enforce pinning if desired
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
