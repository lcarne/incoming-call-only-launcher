package com.callonly.launcher.ui.home

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.callonly.launcher.ui.components.BatteryLevelDisplay
import com.callonly.launcher.ui.components.NetworkSignalDisplay
import com.callonly.launcher.ui.theme.HighContrastButtonBg
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAdminClick: () -> Unit
) {
    // Clock State
    var currentTime by remember { mutableStateOf(Date()) }
    
    // Settings State
    val isAlwaysOn by viewModel.isAlwaysOnEnabled.collectAsState()
    val nightStart by viewModel.nightModeStartHour.collectAsState()
    val nightStartMin by viewModel.nightModeStartMinute.collectAsState()
    val nightEnd by viewModel.nightModeEndHour.collectAsState()
    val nightEndMin by viewModel.nightModeEndMinute.collectAsState()
    val isNightModeEnabled by viewModel.isNightModeEnabled.collectAsState()
    val savedClockColor by viewModel.clockColor.collectAsState()

    val clockColor = if (savedClockColor != 0) Color(savedClockColor) else HighContrastButtonBg
    
    val context = LocalContext.current
    // Contacts removed from Home Screen as requested
    
    // Fake Sleep Logic
    val calendar = java.util.Calendar.getInstance()
    calendar.time = currentTime
    val currentTotalMinutes = calendar.get(java.util.Calendar.HOUR_OF_DAY) * 60 + calendar.get(java.util.Calendar.MINUTE)
    
    val startTotalMinutes = nightStart * 60 + nightStartMin
    val endTotalMinutes = nightEnd * 60 + nightEndMin

    val isNightInSchedule = if (startTotalMinutes < endTotalMinutes) {
        currentTotalMinutes in startTotalMinutes until endTotalMinutes
    } else {
        currentTotalMinutes >= startTotalMinutes || currentTotalMinutes < endTotalMinutes
    }
    val isNight = isNightModeEnabled && isNightInSchedule

    // Track previous night mode state to detect transitions
    var previousIsNight by remember { mutableStateOf(isNight) }

    // Screen Keep On Logic
    // Helper to find Activity from Context (handles Hilt/Compose wrappers)
    fun android.content.Context.findActivity(): android.app.Activity? {
        var currentContext = this
        while (currentContext is android.content.ContextWrapper) {
            if (currentContext is android.app.Activity) return currentContext
            currentContext = currentContext.baseContext
        }
        return null
    }

    LaunchedEffect(isAlwaysOn, isNight, currentTime) {
        val activity = context.findActivity()
        if (activity != null) {
            // Detect transition from night to day
            val isTransitioningFromNightToDay = previousIsNight && !isNight
            
            // Clear flags first to avoid conflicts
            activity.window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            activity.window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
            
            if (isAlwaysOn && !isNight) {
                // If transitioning from night to day, actively wake the screen
                if (isTransitioningFromNightToDay) {
                    viewModel.wakeUpScreen() // Use PowerManager WakeLock to wake from deep sleep
                }
                // Keep screen on
                activity.window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
            
            // Update previous state
            previousIsNight = isNight
        }
    }
    

    // Force Ringer OFF when Night Mode is active
    LaunchedEffect(isNight) {
        if (isNight) {
            viewModel.setRingerEnabled(false)
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Date()
            val clockCalendar = java.util.Calendar.getInstance()
            val seconds = clockCalendar.get(java.util.Calendar.SECOND)
            delay((60 - seconds) * 1000L)
        }
    }

    val timeFormatPattern = remember {
        mutableStateOf("HH:mm")
    }
    val savedFormat by viewModel.timeFormat.collectAsState()
    LaunchedEffect(savedFormat) {
        timeFormatPattern.value = if (savedFormat == "12") "hh:mm a" else "HH:mm"
    }
    val timeFormat = SimpleDateFormat(timeFormatPattern.value, Locale.getDefault())
    val dateFormat = SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault())

    // High Contrast / Black Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(com.callonly.launcher.ui.theme.Black),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Battery and Network
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                 NetworkSignalDisplay()
                 Spacer(modifier = Modifier.width(32.dp))
                 BatteryLevelDisplay()
            }
            
            // Date
            val rawDate = dateFormat.format(currentTime)
            val capitalizedDate = rawDate.split(" ").joinToString(" ") { 
                if (it.firstOrNull()?.isLetter() == true) it.replaceFirstChar { char -> char.uppercase() } else it 
            }

            Text(
                text = capitalizedDate,
                style = MaterialTheme.typography.displayMedium.copy(fontSize = 30.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                color = com.callonly.launcher.ui.theme.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            try {
                                // Long press of 20 seconds to enter Admin
                                withTimeout(20000) {
                                    awaitRelease()
                                }
                            } catch (e: TimeoutCancellationException) {
                                onAdminClick()
                            }
                        }
                    )
                }
            )

            if (savedFormat == "12") {
                val timeOnly = SimpleDateFormat("hh:mm", Locale.getDefault()).format(currentTime)
                val ampm = SimpleDateFormat("a", Locale.getDefault()).format(currentTime)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = timeOnly,
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 120.sp),
                        color = clockColor,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = ampm,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 28.sp),
                        color = clockColor,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Text(
                    text = timeFormat.format(currentTime),
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 120.sp),
                    color = clockColor,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // Ringer Toggle Button
            val isRingerEnabled by viewModel.isRingerEnabled.collectAsState()
            
            androidx.compose.material3.FilledTonalButton(
                onClick = { 
                    if (!isNight) {
                        viewModel.setRingerEnabled(!isRingerEnabled) 
                    }
                },
                modifier = Modifier
                    .width(300.dp)
                    .height(160.dp), // Increased to 160.dp to accommodate 3-line English text
                enabled = !isNight, // Optional: visually disable it, or keep enabled but show "Action not allowed" toast. User asked for "pas possible de passer en mode active"
                colors = androidx.compose.material3.ButtonDefaults.filledTonalButtonColors(
                    containerColor = if (isNight) com.callonly.launcher.ui.theme.ErrorRed.copy(alpha = 0.5f) else if (isRingerEnabled) clockColor else com.callonly.launcher.ui.theme.ErrorRed,
                    contentColor = if (isNight) com.callonly.launcher.ui.theme.White.copy(alpha = 0.5f) else if (isRingerEnabled) com.callonly.launcher.ui.theme.Black else com.callonly.launcher.ui.theme.White,
                    disabledContainerColor = com.callonly.launcher.ui.theme.ErrorRed.copy(alpha = 0.3f), // If we use enabled=false
                    disabledContentColor = com.callonly.launcher.ui.theme.White.copy(alpha = 0.5f)
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(32.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (isNight) {
                         Icon(
                            imageVector = com.callonly.launcher.ui.theme.StatusIcons.VolumeOff,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp) // Reduced from 56.dp to save vertical space
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(
                                id = com.callonly.launcher.R.string.night_mode_active_ringer_off,
                                String.format("%02dh%02d", nightStart, nightStartMin),
                                String.format("%02dh%02d", nightEnd, nightEndMin)
                            ),
                            fontSize = 18.sp, // Slightly increased
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    } else {
                        Icon(
                            imageVector = if (isRingerEnabled) com.callonly.launcher.ui.theme.StatusIcons.VolumeUp else com.callonly.launcher.ui.theme.StatusIcons.VolumeOff,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp) // Increased from 40.dp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isRingerEnabled) stringResource(id = com.callonly.launcher.R.string.ringer_active) else stringResource(id = com.callonly.launcher.R.string.ringer_disabled),
                            fontSize = 24.sp, // Increased from 20.sp
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Default Dialer Check
            val isDefaultDialer by viewModel.isDefaultDialer.collectAsState()
            val lifecycleOwner = LocalLifecycleOwner.current
            
            DisposableEffect(lifecycleOwner, isDefaultDialer) {
                val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
                    // Only check if it's currently false (e.g. user just came back from settings to fix it)
                    if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME && !isDefaultDialer) {
                        viewModel.refreshDialerStatus(context)
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            // One-time check on first load
            LaunchedEffect(Unit) {
                viewModel.refreshDialerStatus(context)
            }

            if (!isDefaultDialer) {
                val dialerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult(),
                    onResult = { }
                )

                Button(
                    onClick = {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            val roleManager = context.getSystemService(Context.ROLE_SERVICE) as android.app.role.RoleManager
                            val intent = roleManager.createRequestRoleIntent(android.app.role.RoleManager.ROLE_DIALER)
                            dialerLauncher.launch(intent)
                        } else {
                            val intent = Intent(android.telecom.TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).apply {
                                putExtra(android.telecom.TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, context.packageName)
                            }
                            dialerLauncher.launch(intent)
                        }
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = com.callonly.launcher.ui.theme.ErrorRed),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(stringResource(id = com.callonly.launcher.R.string.activate_calls), fontSize = 24.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                }
            }
        }
    }
}


