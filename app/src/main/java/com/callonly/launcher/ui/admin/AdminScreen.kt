package com.callonly.launcher.ui.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AdminScreen(
    onExit: () -> Unit,
    onUnpin: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()

    if (!isAuthenticated) {
        PinEntryScreen(
            viewModel = viewModel,
            onExit = onExit
        )
    } else {
        AdminContent(
            viewModel = viewModel,
            onExit = onExit,
            onUnpin = onUnpin
        )
    }
}

@Composable
fun AdminContent(
    viewModel: AdminViewModel,
    onExit: () -> Unit,
    onUnpin: () -> Unit
) {
    var currentView by remember { mutableStateOf("SETTINGS") } // SETTINGS, CONTACTS, or HISTORY
    var pendingPhotoCaptured: ((android.net.Uri) -> Unit)? by remember { mutableStateOf(null) }
    var isCameraOpen by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentView) {
            "CONTACTS" -> {
                ContactManagementScreen(
                    viewModel = viewModel,
                    onBack = { currentView = "SETTINGS" },
                    onOpenCamera = { onCaptured ->
                        pendingPhotoCaptured = onCaptured
                        isCameraOpen = true
                    }
                )
            }

            "HISTORY" -> {
                CallHistoryScreen(
                    viewModel = viewModel,
                    onBack = { currentView = "SETTINGS" }
                )
            }

            else -> {
                AdminSettingsScreen(
                    viewModel = viewModel,
                    onExit = onExit,
                    onUnpin = onUnpin,
                    onManageContacts = { currentView = "CONTACTS" },
                    onShowHistory = { currentView = "HISTORY" }
                )
            }
        }

        if (isCameraOpen) {
            Dialog(
                onDismissRequest = {
                    isCameraOpen = false
                    pendingPhotoCaptured = null
                },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = false
                )
            ) {
                CameraScreen(
                    onPhotoCaptured = { uri ->
                        pendingPhotoCaptured?.invoke(uri)
                        isCameraOpen = false
                        pendingPhotoCaptured = null
                    },
                    onCancel = {
                        isCameraOpen = false
                        pendingPhotoCaptured = null
                    }
                )
            }
        }
    }
}
