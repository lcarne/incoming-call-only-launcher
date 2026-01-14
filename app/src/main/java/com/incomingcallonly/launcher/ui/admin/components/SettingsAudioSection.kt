package com.incomingcallonly.launcher.ui.admin.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.incomingcallonly.launcher.R
import com.incomingcallonly.launcher.ui.admin.AdminViewModel
import com.incomingcallonly.launcher.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsAudioSection(viewModel: AdminViewModel) {
    Column {
        AdminSectionHeader(text = stringResource(id = R.string.settings_section_audio)) // Assuming you add this string, or reuse an existing one if appropriate. Wait, the original didn't have a header call here but AdminSettingsScreen might have been updated. 
        // Actually AdminSettingsScreen calls SettingsAudioSection directly.
        // Let's check original SettingsAudioSection ... it didn't have a header text inside. 
        // I should probably add one for consistency if it fits, or merge into System if that feels better.
        // But "System" and "Audio" were separate.
        // I'll add a header for "Audio".
        
        AdminSettingsCard {
            // Audio Output
            val isDefaultSpeakerEnabled by viewModel.isDefaultSpeakerEnabled.collectAsState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.default_audio_output),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                ModernSegmentedButton(
                    options = listOf(
                        stringResource(id = R.string.earpiece_setting),
                        stringResource(id = R.string.speaker_setting)
                    ),
                    selectedIndex = if (isDefaultSpeakerEnabled) 1 else 0,
                    onOptionSelected = { index ->
                        viewModel.setDefaultSpeakerEnabled(index == 1)
                    }
                )
            }

            AdminDivider()
            
            // Ringer Volume with Enhanced Display
            val ringerVolume by viewModel.ringerVolume.collectAsState()
            ListItem(
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                headlineContent = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(id = R.string.ringer_volume))
                        // Inline percentage display
                        Text(
                            text = "$ringerVolume%",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                },
                supportingContent = {
                    Column {
                        Slider(
                            value = ringerVolume.toFloat(),
                            onValueChange = { viewModel.setRingerVolume(it.toInt()) },
                            valueRange = 0f..100f,
                            steps = 9,  // 10% increments for easier control
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        // Min/max labels for context
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "0%",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "100%",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                trailingContent = {
                    IconButton(
                        onClick = { viewModel.testRingtone() },
                        modifier = Modifier.size(Spacing.iconButtonSize)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = stringResource(id = R.string.test_ringer),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(Spacing.iconLarge)
                        )
                    }
                }
            )
        }
    }
}
