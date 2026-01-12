package com.callonly.launcher.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.callonly.launcher.R
import com.callonly.launcher.ui.admin.dialogs.ScreenBehaviorDialog
import com.callonly.launcher.ui.admin.dialogs.TimePickerDialogWrapper
import com.callonly.launcher.ui.theme.HighContrastButtonBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSettingsScreen(
    viewModel: AdminViewModel,
    onExit: () -> Unit,
    onUnpin: () -> Unit,
    onManageContacts: () -> Unit,
    onShowHistory: () -> Unit
) {
    val scrollState = rememberScrollState()

    val exportLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.CreateDocument("application/json"),
        onResult = { uri ->
            uri?.let { viewModel.exportContacts(it) }
        }
    )

    val importLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let { viewModel.importContacts(it) }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.settings)) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // General Actions
            ListItem(
                headlineContent = { Text(stringResource(id = R.string.unlock)) },
                leadingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lock_open),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                modifier = Modifier
                    .clickable {
                        onUnpin()
                        viewModel.logout()
                        onExit()
                    }
            )
            ListItem(
                headlineContent = { Text(stringResource(id = R.string.back_arrow)) },
                leadingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = null
                    )
                },
                modifier = Modifier.clickable {
                    viewModel.logout()
                    onExit()
                }
            )

            HorizontalDivider()

            // Content Management
            Text(
                text = stringResource(id = R.string.settings_section_content),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            ListItem(
                headlineContent = { Text(stringResource(id = R.string.manage_contacts)) },
                leadingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_list),
                        contentDescription = null
                    )
                },
                trailingContent = {
                     Icon(
                        imageVector = Icons.Default.PlayArrow, // Using PlayArrow as generic forward/action indicator for now or null
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                     )
                },
                modifier = Modifier.clickable(onClick = onManageContacts)
            )

            ListItem(
                headlineContent = { Text(stringResource(id = R.string.call_history)) },
                leadingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_call),
                        contentDescription = null
                    )
                },
                trailingContent = {
                     Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                     )
                },
                modifier = Modifier.clickable(onClick = onShowHistory)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Settings Section (Refactored below)
            SettingsSection(viewModel)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Data Management
            Text(
                text = stringResource(id = R.string.data_management),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            ListItem(
                headlineContent = { Text(stringResource(id = R.string.export_contacts)) },
                leadingContent = { Icon(Icons.Default.Share, contentDescription = null) },
                modifier = Modifier.clickable { exportLauncher.launch("contacts_backup.json") }
            )

            ListItem(
                headlineContent = { Text(stringResource(id = R.string.import_contacts)) },
                leadingContent = { Icon(Icons.Default.Add, contentDescription = null) },
                modifier = Modifier.clickable { importLauncher.launch(arrayOf("application/json")) }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingsSection(viewModel: AdminViewModel) {
    val isNightModeEnabled by viewModel.isNightModeEnabled.collectAsState()
    val nightStart by viewModel.nightModeStartHour.collectAsState()
    val nightStartMin by viewModel.nightModeStartMinute.collectAsState()
    val nightEnd by viewModel.nightModeEndHour.collectAsState()
    val nightEndMin by viewModel.nightModeEndMinute.collectAsState()


    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.settings_section_system),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
        )

        // Call Security
        val allowAllCalls by viewModel.allowAllCalls.collectAsState()
        ListItem(
            headlineContent = { Text(stringResource(id = R.string.accept_all_calls)) },
            supportingContent = { Text(stringResource(id = R.string.accept_all_calls_desc)) },
            trailingContent = {
                Switch(
                    checked = allowAllCalls,
                    onCheckedChange = { viewModel.setAllowAllCalls(it) }
                )
            }
        )

        // Audio Output
        val isDefaultSpeakerEnabled by viewModel.isDefaultSpeakerEnabled.collectAsState()
        ListItem(
            headlineContent = { Text(stringResource(id = R.string.default_audio_output)) },
            supportingContent = {
               SingleChoiceSegmentedButtonRow(
                   modifier = Modifier.padding(top = 8.dp)
               ) {
                   SegmentedButton(
                       selected = !isDefaultSpeakerEnabled,
                       onClick = { viewModel.setDefaultSpeakerEnabled(false) },
                       shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                   ) {
                       Text(
                           text = stringResource(id = R.string.earpiece),
                           style = MaterialTheme.typography.labelSmall,
                           textAlign = TextAlign.Center
                       )
                   }
                   SegmentedButton(
                       selected = isDefaultSpeakerEnabled,
                       onClick = { viewModel.setDefaultSpeakerEnabled(true) },
                       shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                   ) {
                       Text(
                           text = stringResource(id = R.string.speaker),
                           style = MaterialTheme.typography.labelSmall,
                           textAlign = TextAlign.Center
                       )
                   }
               }
            }
        )

        // Ringer Active
        val isRingerEnabled by viewModel.isRingerEnabled.collectAsState()
        ListItem(
            headlineContent = { Text(stringResource(id = R.string.ringer_active)) },
            trailingContent = {
                Switch(
                    checked = isRingerEnabled,
                    onCheckedChange = { viewModel.setRingerEnabled(it) }
                )
            }
        )

        // Ringer Volume (Only show slider if ringer enabled or generally available? Usually available)
        val ringerVolume by viewModel.ringerVolume.collectAsState()
        ListItem(
            headlineContent = { Text(stringResource(id = R.string.ringer_volume)) },
            supportingContent = {
                Column {
                    Slider(
                        value = ringerVolume.toFloat(),
                        onValueChange = { viewModel.setRingerVolume(it.toInt()) },
                        valueRange = 0f..100f
                    )
                }
            },
            trailingContent = {
                IconButton(onClick = { viewModel.testRingtone() }) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = stringResource(id = R.string.test_ringer),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        // Screen & Display
        Text(
            text = stringResource(id = R.string.settings_section_display_power),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
        )

        // Plugged Behavior
        val screenBehaviorPlugged by viewModel.screenBehaviorPlugged.collectAsState()
        var showPluggedDialog by remember { mutableStateOf(false) }
        ListItem(
            headlineContent = { Text(stringResource(id = R.string.screen_behavior_plugged)) },
            leadingContent = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_battery_charging),
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

        // Battery Behavior
        val screenBehaviorBattery by viewModel.screenBehaviorBattery.collectAsState()
        var showBatteryDialog by remember { mutableStateOf(false) }
        ListItem(
            headlineContent = { Text(stringResource(id = R.string.screen_behavior_battery)) },
            leadingContent = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_battery_full),
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

        // Night Mode
        ListItem(
            headlineContent = { Text(stringResource(id = R.string.night_mode)) },
            supportingContent = { Text(stringResource(id = R.string.night_mode_desc)) },
            trailingContent = {
                Switch(
                    checked = isNightModeEnabled,
                    onCheckedChange = { viewModel.setNightModeEnabled(it) }
                )
            }
        )

        if (isNightModeEnabled) {
            var showStartPicker by remember { mutableStateOf(false) }
            var showEndPicker by remember { mutableStateOf(false) }

            // Indent for nested settings
            Column(modifier = Modifier.padding(start = 16.dp)) {
                ListItem(
                    headlineContent = { Text(stringResource(id = R.string.night_start_label)) },
                    trailingContent = {
                        Text(
                            text = String.format("%02dh%02d", nightStart, nightStartMin),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.clickable { showStartPicker = true }
                )
                ListItem(
                    headlineContent = { Text(stringResource(id = R.string.night_end_label)) },
                    trailingContent = {
                         Text(
                            text = String.format("%02dh%02d", nightEnd, nightEndMin),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
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
                    
                Text(
                     text = stringResource(
                        id = R.string.night_mode_duration_desc,
                        durationHours,
                        if (durationMinsOnly > 0) String.format("%02d", durationMinsOnly) else "",
                        nextDay
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            if (showStartPicker) {
                TimePickerDialogWrapper(
                    initialHour = nightStart,
                    initialMinute = nightStartMin,
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
                    onDismiss = { showEndPicker = false },
                    onConfirm = { hour, minute ->
                        viewModel.setNightModeEndHour(hour)
                        viewModel.setNightModeEndMinute(minute)
                        showEndPicker = false
                    }
                )
            }
        }
        
        // Clock Color
        val colors = listOf(
            MaterialTheme.colorScheme.primary,       // M3 Primary
            MaterialTheme.colorScheme.secondary,     // M3 Secondary
            MaterialTheme.colorScheme.tertiary,      // M3 Tertiary
            Color(0xFFD32F2F),                       // Soft Red
            Color(0xFFC2185B),                       // Pink
            Color(0xFF7B1FA2),                       // Purple
            Color(0xFF1976D2),                       // Blue
            Color(0xFF388E3C),                       // Green
            Color(0xFFFBC02D),                       // Amber/Yellow
            Color.White                              // Pure White
        )
        val currentColor =
            if (viewModel.clockColor.collectAsState().value != 0) Color(viewModel.clockColor.collectAsState().value) else HighContrastButtonBg

        ListItem(
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
                                    width = 2.dp,
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

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        // Language & Format
        Text(
            text = stringResource(id = R.string.settings_section_localization),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
        )

        var showLangDialog by remember { mutableStateOf(false) }
        ListItem(
            headlineContent = { Text(stringResource(id = R.string.language)) },
            supportingContent = {
                 val lang by viewModel.language.collectAsState()
                 Text(if (lang == "fr") "Français" else "English")
            },
            modifier = Modifier.clickable { showLangDialog = true }
        )

        val currentFormat by viewModel.timeFormat.collectAsState()
        ListItem(
            headlineContent = { Text(stringResource(id = R.string.time_format_title)) },
            supportingContent = {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    SegmentedButton(
                        selected = currentFormat == "12",
                        onClick = { viewModel.setTimeFormat("12") },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) {
                        Text(
                            text = stringResource(id = R.string.time_format_12),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    SegmentedButton(
                        selected = currentFormat == "24",
                        onClick = { viewModel.setTimeFormat("24") },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) {
                        Text(
                            text = stringResource(id = R.string.time_format_24),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        )

        // Dialogs
        if (showPluggedDialog) {
            ScreenBehaviorDialog(
                title = stringResource(id = R.string.screen_behavior_plugged),
                iconRes = R.drawable.ic_battery_charging,
                currentValue = screenBehaviorPlugged,
                onConfirm = { viewModel.setScreenBehaviorPlugged(it); showPluggedDialog = false },
                onDismiss = { showPluggedDialog = false }
            )
        }

        if (showBatteryDialog) {
            ScreenBehaviorDialog(
                title = stringResource(id = R.string.screen_behavior_battery),
                iconRes = R.drawable.ic_battery_full,
                currentValue = screenBehaviorBattery,
                onConfirm = { viewModel.setScreenBehaviorBattery(it); showBatteryDialog = false },
                onDismiss = { showBatteryDialog = false }
            )
        }
        
        if (showLangDialog) {
             val lang by viewModel.language.collectAsState()
             val context = LocalContext.current
             AlertDialog(
                 onDismissRequest = { showLangDialog = false },
                 title = { 
                     Text(
                         stringResource(id = R.string.language),
                         style = MaterialTheme.typography.headlineSmall
                     ) 
                 },
                 text = {
                     Column(
                         modifier = Modifier.fillMaxWidth(),
                         verticalArrangement = Arrangement.spacedBy(8.dp)
                     ) {
                         listOf(
                             Triple("fr", stringResource(id = R.string.language_french), "🇫🇷"),
                             Triple("en", stringResource(id = R.string.language_english), "🇬🇧")
                         ).forEach { (code, label, flag) ->
                             val isSelected = lang == code
                             Surface(
                                 onClick = {
                                     viewModel.setLanguage(code)
                                     showLangDialog = false
                                     (context as? android.app.Activity)?.recreate()
                                 },
                                 shape = RoundedCornerShape(12.dp),
                                 color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                 modifier = Modifier.fillMaxWidth()
                             ) {
                                 Row(
                                     modifier = Modifier
                                         .padding(horizontal = 16.dp, vertical = 12.dp),
                                     verticalAlignment = Alignment.CenterVertically
                                 ) {
                                     Text(
                                         text = flag,
                                         fontSize = 28.sp,
                                         modifier = Modifier.size(32.dp),
                                         textAlign = TextAlign.Center
                                     )
                                     Spacer(modifier = Modifier.width(16.dp))
                                     Text(
                                         text = label,
                                         style = MaterialTheme.typography.bodyLarge,
                                         color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                         modifier = Modifier.weight(1f)
                                     )
                                     RadioButton(
                                         selected = isSelected,
                                         onClick = null
                                     )
                                 }
                             }
                         }
                     }
                 },
                 confirmButton = {},
                 dismissButton = {
                     TextButton(onClick = { showLangDialog = false }) {
                         Text(stringResource(id = R.string.cancel))
                     }
                 }
             )
        }
    }
}
