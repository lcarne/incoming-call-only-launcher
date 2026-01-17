package com.incomingcallonly.launcher.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect

/**
 * Sets the color of the system bars (status bar and navigation bar).
 * 
 * @param statusBarColor Color for the status bar
 * @param navigationBarColor Color for the navigation bar
 * @param darkIcons Whether to use dark icons (true for light backgrounds, false for dark backgrounds)
 */
@Composable
fun SystemBarsColor(
    darkIcons: Boolean = false
) {
    val view = LocalView.current
    
    // Lambda to apply system bar colors
    val applySystemBarsColors: () -> Unit = applyColors@ {
        val window = view.context.findActivity()?.window ?: return@applyColors
        @Suppress("DEPRECATION")
        window.statusBarColor = Color.Transparent.toArgb()
        @Suppress("DEPRECATION")
        window.navigationBarColor = Color.Transparent.toArgb()
        
        val windowInsetsController = WindowCompat.getInsetsController(window, view)
        windowInsetsController.isAppearanceLightStatusBars = darkIcons
        windowInsetsController.isAppearanceLightNavigationBars = darkIcons
        
        // Enforce edge-to-edge to ensure background extends behind bars
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
    
    if (!view.isInEditMode) {
        // Apply on initial composition
        SideEffect {
            applySystemBarsColors()
        }
        
        // Re-apply when activity resumes (e.g., returning from file picker)
        LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
            applySystemBarsColors()
        }
    }
}

private tailrec fun Context.findActivity(): Activity? {
    if (this is Activity) return this
    if (this is ContextWrapper) return baseContext.findActivity()
    return null
}
