package com.callonly.launcher.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.callonly.launcher.ui.admin.AdminScreen
import com.callonly.launcher.ui.home.HomeScreen
import com.callonly.launcher.data.model.Contact
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CallOnlyNavGraph(
    onCall: (Contact) -> Unit, // Callback to handle actual calling
    onUnpin: () -> Unit // Callback to exit Kiosk mode
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            val viewModel = hiltViewModel<com.callonly.launcher.ui.home.HomeViewModel>()
            HomeScreen(
                viewModel = viewModel,
                onAdminClick = { navController.navigate("admin") }
            )
        }
        composable("admin") {
            AdminScreen(
                onExit = { navController.popBackStack() },
                onUnpin = onUnpin
            )
        }
    }
}
