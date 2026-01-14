package com.incomingcallonly.launcher.ui.home

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.incomingcallonly.launcher.ui.components.BatteryLevelDisplay
import com.incomingcallonly.launcher.ui.components.NetworkSignalDisplay
import com.incomingcallonly.launcher.ui.home.components.ClockDisplay
import com.incomingcallonly.launcher.ui.home.components.DefaultAppPrompts
import com.incomingcallonly.launcher.ui.home.components.RingerControl
import com.incomingcallonly.launcher.ui.onboarding.OnboardingFlow
import com.incomingcallonly.launcher.ui.theme.Black
import com.incomingcallonly.launcher.ui.theme.HighContrastButtonBg
import com.incomingcallonly.launcher.ui.theme.White
import com.incomingcallonly.launcher.ui.theme.DimmedClockColor
import android.view.WindowManager.LayoutParams
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val CLOCK_DATE_FORMAT = "EEEE d MMMM yyyy"
private const val ADMIN_TAP_THRESHOLD = 15
private const val ADMIN_TAP_TIMEOUT_MS = 1000L
private const val BRIGHTNESS_DIM = 0.01f
private const val INACTIVITY_TIMEOUT_MS = 300L // Note: Code logic uses 300ms currently

// Behavior States
private const val BEHAVIOR_OFF = 0
private const val BEHAVIOR_DIM = 1
private const val BEHAVIOR_AWAKE = 2
private const val MINUTES_IN_HOUR = 60

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAdminClick: () -> Unit
) {
    // Clock State
    var currentTime by remember { mutableStateOf(Date()) }

    // Settings State
    val screenBehaviorPlugged by viewModel.screenBehaviorPlugged.collectAsState(initial = 0)
    val screenBehaviorBattery by viewModel.screenBehaviorBattery.collectAsState(initial = 0)
    val nightStart by viewModel.nightModeStartHour.collectAsState()
    val nightStartMin by viewModel.nightModeStartMinute.collectAsState()
    val nightEnd by viewModel.nightModeEndHour.collectAsState()
    val nightEndMin by viewModel.nightModeEndMinute.collectAsState()
    val isNightModeEnabled by viewModel.isNightModeEnabled.collectAsState()
    val savedClockColor by viewModel.clockColor.collectAsState()
    val hasSeenOnboarding by viewModel.hasSeenOnboarding.collectAsState()
    val isRingerEnabled by viewModel.isRingerEnabled.collectAsState()

    val clockColor = if (savedClockColor != 0) Color(savedClockColor) else HighContrastButtonBg

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Fake Sleep Logic
    val calendar = java.util.Calendar.getInstance()
    calendar.time = currentTime
    val currentTotalMinutes =
        calendar.get(java.util.Calendar.HOUR_OF_DAY) * MINUTES_IN_HOUR + calendar.get(java.util.Calendar.MINUTE)

    val startTotalMinutes = nightStart * MINUTES_IN_HOUR + nightStartMin
    val endTotalMinutes = nightEnd * MINUTES_IN_HOUR + nightEndMin

    val isNightInSchedule = if (startTotalMinutes < endTotalMinutes) {
        currentTotalMinutes in startTotalMinutes until endTotalMinutes
    } else {
        currentTotalMinutes >= startTotalMinutes || currentTotalMinutes < endTotalMinutes
    }
    val isNight = isNightModeEnabled && isNightInSchedule

    // Track previous night mode state to detect transitions
    var previousIsNight by remember { mutableStateOf(isNight) }

    // Dim Mode State
    var lastInteractionTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var isDimmed by remember { mutableStateOf(false) }

    // Admin Access State
    var adminTapCount by remember { mutableStateOf(0) }
    var lastTapTime by remember { mutableStateOf(0L) }

    // Battery / Power State Monitoring
    var isPlugged by remember { mutableStateOf(false) }
    DisposableEffect(context) {
        val receiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    val plugged = it.getIntExtra(android.os.BatteryManager.EXTRA_PLUGGED, -1)
                    isPlugged = plugged == android.os.BatteryManager.BATTERY_PLUGGED_AC ||
                            plugged == android.os.BatteryManager.BATTERY_PLUGGED_USB ||
                            plugged == android.os.BatteryManager.BATTERY_PLUGGED_WIRELESS
                }
            }
        }
        val filter = android.content.IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val intent = context.registerReceiver(receiver, filter)
        intent?.let {
            val plugged = it.getIntExtra(android.os.BatteryManager.EXTRA_PLUGGED, -1)
            isPlugged = plugged == android.os.BatteryManager.BATTERY_PLUGGED_AC ||
                    plugged == android.os.BatteryManager.BATTERY_PLUGGED_USB ||
                    plugged == android.os.BatteryManager.BATTERY_PLUGGED_WIRELESS
        }
        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    // Determine current behavior based on power state
    // 0 = OFF, 1 = DIM, 2 = AWAKE
    val currentBehavior = if (isPlugged) screenBehaviorPlugged else screenBehaviorBattery

    // Inactivity Monitor
    LaunchedEffect(currentTime, currentBehavior, isNight) {
        // Only monitor for dimming if we are in DIM mode (1) and not in Night Mode
        if (currentBehavior == BEHAVIOR_DIM && !isNight) {
            val timeSinceLastInteraction = System.currentTimeMillis() - lastInteractionTime
            if (timeSinceLastInteraction > INACTIVITY_TIMEOUT_MS) { 
                isDimmed = true
            }
        } else {
            isDimmed = false
        }
    }

    // Screen Keep On & Brightness Logic
    fun Context.findActivity(): android.app.Activity? {
        var currentContext = this
        while (currentContext is android.content.ContextWrapper) {
            if (currentContext is android.app.Activity) return currentContext
            currentContext = currentContext.baseContext
        }
        return null
    }

    LaunchedEffect(currentBehavior, isNight, currentTime, isDimmed) {
        val activity = context.findActivity()
        if (activity != null) {
            val isTransitioningFromNightToDay = previousIsNight && !isNight

            activity.window.clearFlags(LayoutParams.FLAG_KEEP_SCREEN_ON)

            val params = activity.window.attributes

            if (!isNight) {
                if (isTransitioningFromNightToDay && currentBehavior != BEHAVIOR_OFF) {
                    viewModel.wakeUpScreen()
                }

                when (currentBehavior) {
                    BEHAVIOR_OFF -> { // OFF
                        // Standard Android timeout behavior
                        params.screenBrightness =
                            LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                    }

                    BEHAVIOR_DIM -> { // DIM
                        // Keep screen on
                        activity.window.addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON)
                        if (isDimmed) {
                            params.screenBrightness = BRIGHTNESS_DIM // Lowest brightness
                        } else {
                            params.screenBrightness =
                                LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                        }
                    }

                    BEHAVIOR_AWAKE -> { // AWAKE
                        activity.window.addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON)
                        params.screenBrightness =
                            LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                    }
                }
            } else {
                params.screenBrightness =
                    LayoutParams.BRIGHTNESS_OVERRIDE_NONE
            }
            activity.window.attributes = params

            previousIsNight = isNight
        }
    }

    var previousIsNightForRinger by remember { mutableStateOf(isNight) }
    LaunchedEffect(isNight) {
        if (isNight) {
            if (!previousIsNightForRinger) {
                viewModel.saveRingerStatePreNight(isRingerEnabled)
            }
            viewModel.setRingerEnabled(false)
        } else {
            if (previousIsNightForRinger) {
                viewModel.restoreRingerStatePreNight()
            }
        }
        previousIsNightForRinger = isNight
    }

    DisposableEffect(context, lifecycleOwner) {
        val receiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                currentTime = Date()
            }
        }
        val filter = android.content.IntentFilter().apply {
            addAction(Intent.ACTION_TIME_TICK)
            addAction(Intent.ACTION_TIME_CHANGED)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
        }
        context.registerReceiver(receiver, filter)

        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                currentTime = Date()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            context.unregisterReceiver(receiver)
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val dateFormat = SimpleDateFormat(CLOCK_DATE_FORMAT, Locale.getDefault())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        if (event.changes.any { it.changedToDown() }) {
                            lastInteractionTime = System.currentTimeMillis()
                            if (isDimmed) {
                                isDimmed = false
                                event.changes.forEach { it.consume() }
                            }
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {

        if (!isDimmed) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // Battery and Network
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    NetworkSignalDisplay()
                    Spacer(modifier = Modifier.width(32.dp))
                    BatteryLevelDisplay()
                }


                // Date
                val rawDate = dateFormat.format(currentTime)
                val capitalizedDate = rawDate.split(" ").joinToString(" ") {
                    if (it.firstOrNull()
                            ?.isLetter() == true
                    ) it.replaceFirstChar { char -> char.uppercase() } else it
                }

                Text(
                    text = capitalizedDate,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                val now = System.currentTimeMillis()
                                if (now - lastTapTime < ADMIN_TAP_TIMEOUT_MS) {
                                    adminTapCount++
                                } else {
                                    adminTapCount = 1
                                }
                                lastTapTime = now

                                if (adminTapCount >= ADMIN_TAP_THRESHOLD) {
                                    adminTapCount = 0
                                    onAdminClick()
                                }
                            }
                        )
                    }
                )

                // Clock
                val savedFormat by viewModel.timeFormat.collectAsState()
                ClockDisplay(
                    currentTime = currentTime,
                    timeFormat = savedFormat,
                    clockColor = clockColor
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Ringer Toggle Button

                RingerControl(
                    isNight = isNight,
                    isRingerEnabled = isRingerEnabled,
                    nightStart = nightStart,
                    nightStartMin = nightStartMin,
                    nightEnd = nightEnd,
                    nightEndMin = nightEndMin,
                    accentColor = clockColor,
                    timeFormat = savedFormat,
                    onToggleRinger = { viewModel.setRingerEnabled(!isRingerEnabled) }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Default Dialer & Launcher Check
                val isDefaultDialer by viewModel.isDefaultDialer.collectAsState()
                val isDefaultLauncher by viewModel.isDefaultLauncher.collectAsState()

                DisposableEffect(lifecycleOwner) {
                    val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
                        if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                            viewModel.refreshDefaultAppStatus(context)
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                // One-time check on first load
                LaunchedEffect(Unit) {
                    viewModel.refreshDefaultAppStatus(context)
                }

                DefaultAppPrompts(
                    isDefaultDialer = isDefaultDialer,
                    isDefaultLauncher = isDefaultLauncher
                )
            }
        } else {
            // DIMMED VIEW - Only Clock
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // Dimmed Clock
                val savedFormat by viewModel.timeFormat.collectAsState()
                ClockDisplay(
                    currentTime = currentTime,
                    timeFormat = savedFormat,
                    clockColor = DimmedClockColor
                )
            }
        }


        if (!hasSeenOnboarding) {
            OnboardingFlow(
                onDismiss = { viewModel.markOnboardingSeen() }
            )
        }
    }
}


