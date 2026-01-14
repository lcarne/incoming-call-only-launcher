package com.incomingcallonly.launcher.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import android.content.Context
import android.content.ContextWrapper
import android.app.Activity

/**
 * Sets the color of the system bars (status bar and navigation bar).
 * 
 * @param statusBarColor Color for the status bar
 * @param navigationBarColor Color for the navigation bar
 * @param darkIcons Whether to use dark icons (true for light backgrounds, false for dark backgrounds)
 */
@Composable
fun SystemBarsColor(
    statusBarColor: Color? = null,
    navigationBarColor: Color? = null,
    darkIcons: Boolean = false
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = view.context.findActivity()?.window ?: return@SideEffect
            @Suppress("DEPRECATION")
            statusBarColor?.let { window.statusBarColor = it.toArgb() }
            @Suppress("DEPRECATION")
            navigationBarColor?.let { window.navigationBarColor = it.toArgb() }
            
            val windowInsetsController = WindowCompat.getInsetsController(window, view)
            windowInsetsController.isAppearanceLightStatusBars = darkIcons
            windowInsetsController.isAppearanceLightNavigationBars = darkIcons
        }
    }
}

private tailrec fun Context.findActivity(): Activity? {
    if (this is Activity) return this
    if (this is ContextWrapper) return baseContext.findActivity()
    return null
}
