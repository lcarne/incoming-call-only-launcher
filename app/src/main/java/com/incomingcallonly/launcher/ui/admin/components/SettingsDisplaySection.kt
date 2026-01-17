package com.incomingcallonly.launcher.ui.admin.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.incomingcallonly.launcher.R
import com.incomingcallonly.launcher.ui.admin.SettingsViewModel
import com.incomingcallonly.launcher.ui.admin.dialogs.ScreenBehaviorDialog
import com.incomingcallonly.launcher.ui.admin.dialogs.TimePickerDialogWrapper
import com.incomingcallonly.launcher.ui.theme.Spacing
import com.incomingcallonly.launcher.util.TimeFormatUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsDisplaySection(viewModel: SettingsViewModel) {
    // Top-level state hoisting
    val screenBehaviorPlugged by viewModel.screenBehaviorPlugged.collectAsState()
    var showPluggedDialog by remember { mutableStateOf(false) }
    
    val screenBehaviorBattery by viewModel.screenBehaviorBattery.collectAsState()
    var showBatteryDialog by remember { mutableStateOf(false) }
    
    val isNightModeEnabled by viewModel.isNightModeEnabled.collectAsState()

    Column {
        AdminSectionHeader(text = stringResource(id = R.string.settings_section_display_power))

        AdminSettingsCard {
            // Plugged Behavior
            ListItem(
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                headlineContent = { Text(stringResource(id = R.string.screen_behavior_plugged)) },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.BatteryChargingFull,
                        contentDescription = null
                    )
                },
                supportingContent = {
                    Text(
                        when (screenBehaviorPlugged) {
                            0 -> stringResource(id = R.string.mode_off)
                            1 -> stringResource(id = R.string.mode_dim)
                            2 -> stringResource(id = R.string.mode_awake)
                            else -> ""
                        }
                    )
                },
                modifier = Modifier.clickable { showPluggedDialog = true }
            )

            AdminDivider()

            // Battery Behavior
            ListItem(
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                headlineContent = { Text(stringResource(id = R.string.screen_behavior_battery)) },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.BatteryFull,
                        contentDescription = null
                    )
                },
                supportingContent = {
                    Text(
                        when (screenBehaviorBattery) {
                            0 -> stringResource(id = R.string.mode_off)
                            1 -> stringResource(id = R.string.mode_dim)
                            2 -> stringResource(id = R.string.mode_awake)
                            else -> ""
                        }
                    )
                },
                modifier = Modifier.clickable { showBatteryDialog = true }
            )
        }
        
        // Night Mode Group
        AdminSettingsCard {
            ListItem(
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                headlineContent = { Text(stringResource(id = R.string.night_mode)) },
                supportingContent = { Text(stringResource(id = R.string.night_mode_desc)) },
                trailingContent = {
                    AdminSwitch(
                        checked = isNightModeEnabled,
                        onCheckedChange = { viewModel.setNightModeEnabled(it) }
                    )
                }
            )
            
            // Expandable settings INSIDE the card
            AnimatedVisibility(
                visible = isNightModeEnabled,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    AdminDivider()
                    NightModeSettings(viewModel)
                }
            }
        }

        // Clock Color Group
        AdminSettingsCard {
            ClockColorSelector(viewModel)
        }

        // Dialogs
        if (showPluggedDialog) {
            ScreenBehaviorDialog(
                title = stringResource(id = R.string.screen_behavior_plugged),
                icon = Icons.Default.BatteryChargingFull,
                currentValue = screenBehaviorPlugged,
                onConfirm = { viewModel.setScreenBehaviorPlugged(it); showPluggedDialog = false },
                onDismiss = { showPluggedDialog = false }
            )
        }

        if (showBatteryDialog) {
            ScreenBehaviorDialog(
                title = stringResource(id = R.string.screen_behavior_battery),
                icon = Icons.Default.BatteryFull,
                currentValue = screenBehaviorBattery,
                onConfirm = { viewModel.setScreenBehaviorBattery(it); showBatteryDialog = false },
                onDismiss = { showBatteryDialog = false }
            )
        }
    }
}

@Composable
fun NightModeSettings(viewModel: SettingsViewModel) {
    val nightStart by viewModel.nightModeStartHour.collectAsState()
    val nightStartMin by viewModel.nightModeStartMinute.collectAsState()
    val nightEnd by viewModel.nightModeEndHour.collectAsState()
    val nightEndMin by viewModel.nightModeEndMinute.collectAsState()
    val timeFormat by viewModel.timeFormat.collectAsState()
    val is24Hour = timeFormat == "24"

    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    Column {
        ListItem(
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            headlineContent = { Text(stringResource(id = R.string.night_start_label)) },
            trailingContent = {
                Text(
                    text = TimeFormatUtils.formatTime(nightStart, nightStartMin, is24Hour),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            },
            modifier = Modifier.clickable { showStartPicker = true }
        )
        
        AdminDivider() // Divider between start and end
        
        ListItem(
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            headlineContent = { Text(stringResource(id = R.string.night_end_label)) },
            trailingContent = {
                Text(
                    text = TimeFormatUtils.formatTime(nightEnd, nightEndMin, is24Hour),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            },
            modifier = Modifier.clickable { showEndPicker = true }
        )

        // Duration info
        val startTotalMinutes = nightStart * 60 + nightStartMin
        val endTotalMinutes = nightEnd * 60 + nightEndMin
        val durationMinutes =
            if (endTotalMinutes > startTotalMinutes) endTotalMinutes - startTotalMinutes else (24 * 60 - startTotalMinutes) + endTotalMinutes
        val durationHours = durationMinutes / 60
        val durationMinsOnly = durationMinutes % 60
        val nextDay =
            if (endTotalMinutes <= startTotalMinutes) stringResource(id = R.string.next_day) else ""

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.listItemHorizontal, vertical = Spacing.sm)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(12.dp),
             verticalAlignment = Alignment.CenterVertically
        ) {
              Icon(
                  imageVector = Icons.Default.DateRange, // Reusing date range icon as clock/duration icon
                  contentDescription = null,
                  modifier = Modifier.size(16.dp),
                  tint = MaterialTheme.colorScheme.onSurfaceVariant
              )
              androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
              Text(
                text = pluralStringResource(
                    id = R.plurals.night_mode_duration_desc,
                    count = durationHours,
                    durationHours,
                    if (durationMinsOnly > 0) String.format(java.util.Locale.getDefault(), "%02d", durationMinsOnly) else "",
                    nextDay
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (showStartPicker) {
        TimePickerDialogWrapper(
            initialHour = nightStart,
            initialMinute = nightStartMin,
            is24HourFormat = is24Hour,
            onDismiss = { showStartPicker = false },
            onConfirm = { hour, minute ->
                viewModel.setNightModeStartHour(hour)
                viewModel.setNightModeStartMinute(minute)
                showStartPicker = false
            }
        )
    }

    if (showEndPicker) {
        TimePickerDialogWrapper(
            initialHour = nightEnd,
            initialMinute = nightEndMin,
            is24HourFormat = is24Hour,
            onDismiss = { showEndPicker = false },
            onConfirm = { hour, minute ->
                viewModel.setNightModeEndHour(hour)
                viewModel.setNightModeEndMinute(minute)
                showEndPicker = false
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClockColorSelector(viewModel: SettingsViewModel) {
    // Enhanced color palette with vibrant, accessible colors (all WCAG AA compliant on dark backgrounds)
    val colors = listOf(
        // Material You dynamic colors
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        
        // Vibrant, accessible Material 600 colors
        Color(0xFFE53935),  // Red 600
        Color(0xFFD81B60),  // Pink 600
        Color(0xFF8E24AA),  // Purple 600
        Color(0xFF3949AB),  // Indigo 600
        Color(0xFF00ACC1),  // Cyan 600
        Color(0xFF43A047),  // Green 600
        Color(0xFFFDD835),  // Yellow 600 (high luminance)
        Color(0xFFFF6F00),  // Orange 800 (deeper for contrast)
        Color.White         // Pure white
    )
    val storedColorValue by viewModel.clockColor.collectAsState()
    val currentColor = if (storedColorValue != 0) Color(storedColorValue) else MaterialTheme.colorScheme.primary

    ListItem(
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        headlineContent = { Text(stringResource(id = R.string.clock_color)) },
        supportingContent = {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
            ) {
                colors.forEach { color ->
                    val isSelected = color.toArgb() == currentColor.toArgb()
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = 1.dp,
                                color = if (color == Color.White) Color.LightGray else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { viewModel.setClockColor(color.toArgb()) }
                            .border(
                                width = if (isSelected) 3.dp else 0.dp,
                                color = if (isSelected) {
                                    if (color == Color.White) MaterialTheme.colorScheme.primary else Color.White
                                } else Color.Transparent,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = if (color == Color.White) Color.Black else Color.White)
                        }
                    }
                }
            }
        }
    )
}
