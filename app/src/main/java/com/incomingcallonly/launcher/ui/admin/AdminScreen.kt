package com.incomingcallonly.launcher.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
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
    onShowSystemUI: () -> Unit = {},
    onHideSystemUI: () -> Unit = {},
    authViewModel: AuthViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    contactsViewModel: ContactsViewModel = hiltViewModel()
) {
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

    // Show system bars when entering admin screens
    androidx.compose.runtime.DisposableEffect(Unit) {
        onShowSystemUI()
        onDispose {
            onHideSystemUI()
        }
    }

    if (!isAuthenticated) {
        PinEntryScreen(
            viewModel = authViewModel,
            onExit = onExit
        )
    } else {
        AdminContent(
            settingsViewModel = settingsViewModel,
            contactsViewModel = contactsViewModel,
            authViewModel = authViewModel,
            onLogout = { authViewModel.logout() },
            onExit = onExit,
            onUnpin = onUnpin
        )
    }
}

@Composable
fun AdminContent(
    settingsViewModel: SettingsViewModel,
    contactsViewModel: ContactsViewModel,
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
    onExit: () -> Unit,
    onUnpin: () -> Unit
) {
    var currentView by remember { mutableStateOf("SETTINGS") } // SETTINGS, CONTACTS, or HISTORY
    var pendingPhotoCaptured: ((android.net.Uri) -> Unit)? by remember { mutableStateOf(null) }
    var isCameraOpen by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (currentView) {
            "CONTACTS" -> {
                ContactManagementScreen(
                    viewModel = contactsViewModel,
                    onBack = { currentView = "SETTINGS" },
                    onOpenCamera = { onCaptured ->
                        pendingPhotoCaptured = onCaptured
                        isCameraOpen = true
                    }
                )
            }

            "HISTORY" -> {
                CallHistoryScreen(
                    viewModel = settingsViewModel,
                    onBack = { currentView = "SETTINGS" }
                )
            }

            else -> {
                AdminSettingsScreen(
                    settingsViewModel = settingsViewModel,
                    contactsViewModel = contactsViewModel,
                    authViewModel = authViewModel,
                    onExit = onExit,
                    onLogout = onLogout,
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
