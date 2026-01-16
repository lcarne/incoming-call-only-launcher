package com.incomingcallonly.launcher.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.incomingcallonly.launcher.ui.admin.AdminScreen
import com.incomingcallonly.launcher.ui.home.HomeScreen

@Composable
fun IncomingCallOnlyNavGraph(
    onUnpin: () -> Unit, // Callback to exit Kiosk mode
    onPin: () -> Unit = {},
    onShowSystemUI: () -> Unit = {},
    onHideSystemUI: () -> Unit = {}
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            val viewModel = hiltViewModel<com.incomingcallonly.launcher.ui.home.HomeViewModel>()
            HomeScreen(
                viewModel = viewModel,
                onAdminClick = { navController.navigate("admin") },
                onPinClick = onPin
            )
        }
        composable("admin") {
            AdminScreen(
                onExit = { navController.popBackStack() },
                onUnpin = onUnpin,
                onShowSystemUI = onShowSystemUI,
                onHideSystemUI = onHideSystemUI
            )
        }
    }
}
