package com.incomingcallonly.launcher.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.incomingcallonly.launcher.R
import com.incomingcallonly.launcher.data.repository.SettingsRepository
import com.incomingcallonly.launcher.manager.NightModeScheduler
import com.incomingcallonly.launcher.ui.components.AppDialog
import com.incomingcallonly.launcher.ui.components.BatteryLevelDisplay
import com.incomingcallonly.launcher.ui.components.NetworkSignalDisplay
import com.incomingcallonly.launcher.ui.home.components.ClockDisplay
import com.incomingcallonly.launcher.ui.home.components.DateDisplay
import com.incomingcallonly.launcher.ui.home.components.DefaultAppPrompts
import com.incomingcallonly.launcher.ui.home.components.RingerControl
import com.incomingcallonly.launcher.ui.home.effects.RingerEffect
import com.incomingcallonly.launcher.ui.home.effects.ScreenEffect
import com.incomingcallonly.launcher.ui.home.effects.rememberCurrentTime
import com.incomingcallonly.launcher.ui.home.effects.rememberInactivityState
import com.incomingcallonly.launcher.ui.home.effects.rememberIsPlugged
import com.incomingcallonly.launcher.ui.onboarding.OnboardingFlow
import com.incomingcallonly.launcher.ui.theme.Black
import com.incomingcallonly.launcher.ui.theme.ConfirmGreen
import com.incomingcallonly.launcher.ui.theme.DimmedClockColor
import com.incomingcallonly.launcher.ui.theme.SystemBarsColor
import java.util.Calendar

private const val MINUTES_IN_HOUR = 60

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAdminClick: () -> Unit,
    onPinClick: () -> Unit
) {
    // 1. Time & System Monitoring
    val currentTime = rememberCurrentTime()
    val isPlugged = rememberIsPlugged()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // 1.a Pin status
    val isPinned by viewModel.isPinned.collectAsState(initial = false)
    var showPinConfirmation by remember { mutableStateOf(false) }

    // 2. Settings State
    val screenBehaviorPlugged by viewModel.screenBehaviorPlugged.collectAsState(initial = SettingsRepository.SCREEN_BEHAVIOR_AWAKE)
    val screenBehaviorBattery by viewModel.screenBehaviorBattery.collectAsState(initial = SettingsRepository.SCREEN_BEHAVIOR_OFF)
    val currentBehavior = if (isPlugged) screenBehaviorPlugged else screenBehaviorBattery

    val nightStart by viewModel.nightModeStartHour.collectAsState()
    val nightStartMin by viewModel.nightModeStartMinute.collectAsState()
    val nightEnd by viewModel.nightModeEndHour.collectAsState()
    val nightEndMin by viewModel.nightModeEndMinute.collectAsState()
    val isNightModeEnabled by viewModel.isNightModeEnabled.collectAsState()
    val isRingerEnabled by viewModel.isRingerEnabled.collectAsState()
    
    val savedClockColor by viewModel.clockColor.collectAsState()
    val clockColor = if (savedClockColor != 0) Color(savedClockColor) else MaterialTheme.colorScheme.primary
    
    val savedFormat by viewModel.timeFormat.collectAsState()
    val hasSeenOnboarding by viewModel.hasSeenOnboarding.collectAsState()
    
    val isDefaultDialer by viewModel.isDefaultDialer.collectAsState()
    val isDefaultLauncher by viewModel.isDefaultLauncher.collectAsState()

    // 3. Night Mode Logic
    val calendar = Calendar.getInstance().apply { time = currentTime }
    val currentTotalMinutes = calendar.get(Calendar.HOUR_OF_DAY) * MINUTES_IN_HOUR + calendar.get(Calendar.MINUTE)
    val startTotalMinutes = nightStart * MINUTES_IN_HOUR + nightStartMin
    val endTotalMinutes = nightEnd * MINUTES_IN_HOUR + nightEndMin

    val isNightInSchedule = if (startTotalMinutes < endTotalMinutes) {
        currentTotalMinutes in startTotalMinutes until endTotalMinutes
    } else {
        currentTotalMinutes !in endTotalMinutes..<startTotalMinutes
    }
    val isNight = isNightModeEnabled && isNightInSchedule

    // 4. Inactivity & Dimming
    val inactivityState = rememberInactivityState(currentBehavior, isNight, currentTime)
    val isDimmed = inactivityState.isDimmed

    // 5. Effects
    ScreenEffect(viewModel, currentBehavior, isNight, isDimmed)
    RingerEffect(viewModel, isNight, isRingerEnabled)
    
    // Default Apps Check Effect
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.refreshDefaultAppStatus(context)
                viewModel.refreshPinStatus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    LaunchedEffect(Unit) {
        viewModel.refreshDefaultAppStatus(context)
        viewModel.refreshPinStatus()
        // Schedule the night mode end alarm on app start
        NightModeScheduler.scheduleNightModeEnd(context)
    }

    // 6. System Bars Configuration
    SystemBarsColor(
        darkIcons = false // Light icons on dark background
    )

    // 7. UI Composition
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            // Add stable padding for system bars to prevent layout jumps
            .statusBarsPadding()
            .navigationBarsPadding()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        if (event.changes.any { it.changedToDown() }) {
                            val wasDimmed = inactivityState.isDimmed
                            inactivityState.onInteraction()
                            
                            if (wasDimmed) {
                                event.changes.forEach { it.consume() }
                            }
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if (!isDimmed) {
             HomeContentNormal(
                 currentTime = currentTime,
                 clockColor = clockColor,
                 timeFormat = savedFormat,
                 isRingerEnabled = isRingerEnabled,
                 onToggleRinger = { viewModel.setRingerEnabled(!isRingerEnabled) },
                 onAdminClick = onAdminClick,
                 nightStart = nightStart,
                 nightStartMin = nightStartMin,
                 nightEnd = nightEnd,
                 nightEndMin = nightEndMin,
                 isNight = isNight,
                 isDefaultDialer = isDefaultDialer,
                 isDefaultLauncher = isDefaultLauncher,
                 isPinned = isPinned,
                 onPinClick = { showPinConfirmation = true }
             )
        } else {
             HomeContentDimmed(currentTime, savedFormat)
        }

        if (showPinConfirmation) {
            AppDialog(
                onDismissRequest = { showPinConfirmation = false },
                title = stringResource(id = R.string.onboarding_pinned_mode_title),
                message = stringResource(id = R.string.onboarding_pinned_mode_message),
                buttons = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = { showPinConfirmation = false },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(stringResource(id = R.string.not_now))
                        }
                        Button(
                            onClick = {
                                showPinConfirmation = false
                                onPinClick()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ConfirmGreen),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(stringResource(id = R.string.understood))
                        }
                    }
                }
            )
        }

        if (!hasSeenOnboarding) {
            OnboardingFlow(onDismiss = { viewModel.markOnboardingSeen() })
        }
    }
}

@Composable
private fun HomeContentNormal(
    currentTime: java.util.Date,
    clockColor: Color,
    timeFormat: String,
    isRingerEnabled: Boolean,
    onToggleRinger: () -> Unit,
    onAdminClick: () -> Unit,
    nightStart: Int,
    nightStartMin: Int,
    nightEnd: Int,
    nightEndMin: Int,
    isNight: Boolean,
    isDefaultDialer: Boolean,
    isDefaultLauncher: Boolean,
    isPinned: Boolean,
    onPinClick: () -> Unit
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            NetworkSignalDisplay(iconSize = 40.dp)
            Spacer(modifier = Modifier.width(32.dp))
            BatteryLevelDisplay()
        }

        // Date
        DateDisplay(currentTime = currentTime, onAdminClick = onAdminClick)

        // Clock
        ClockDisplay(
            currentTime = currentTime,
            timeFormat = timeFormat,
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
            timeFormat = timeFormat,
            onToggleRinger = onToggleRinger
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Default Apps Prompt
        DefaultAppPrompts(
            isDefaultDialer = isDefaultDialer,
            isDefaultLauncher = isDefaultLauncher,
            isPinned = isPinned,
            onPinClick = onPinClick
        )
    }
} 

@Composable
private fun HomeContentDimmed(
    currentTime: java.util.Date,
    timeFormat: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        ClockDisplay(
            currentTime = currentTime,
            timeFormat = timeFormat,
            clockColor = DimmedClockColor
        )
    }
}
