package com.incomingcallonly.launcher.ui.admin.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.incomingcallonly.launcher.R
import com.incomingcallonly.launcher.ui.admin.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsLocalizationSection(viewModel: AdminViewModel) {
    val lang by viewModel.language.collectAsState()
    val currentFormat by viewModel.timeFormat.collectAsState()
    val context = LocalContext.current

    Column {
        AdminSectionHeader(text = stringResource(id = R.string.settings_section_localization))

        AdminSettingsCard {
            var showLangDialog by remember { mutableStateOf(false) }
            
            ListItem(
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                headlineContent = { Text(stringResource(id = R.string.language)) },
                supportingContent = {
                    Text(if (lang == "fr") "Français" else "English")
                },
                modifier = Modifier.clickable { showLangDialog = true }
            )

            AdminDivider()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.time_format_title),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                ModernSegmentedButton(
                    options = listOf(
                        stringResource(id = R.string.time_format_12),
                        stringResource(id = R.string.time_format_24)
                    ),
                    selectedIndex = if (currentFormat == "12") 0 else 1,
                    onOptionSelected = { index ->
                        viewModel.setTimeFormat(if (index == 0) "12" else "24")
                    }
                )
            }

            if (showLangDialog) {
                val languages = remember {
                    listOf(
                        Triple("fr", R.string.language_french, "🇫🇷"),
                        Triple("en", R.string.language_english, "🇬🇧")
                    )
                }
                
                // Using the new AdminSelectionDialog
                AdminSelectionDialog(
                    title = stringResource(id = R.string.language),
                    options = languages,
                    selectedOption = languages.find { it.first == lang } ?: languages[0],
                    onOptionSelected = { (code, _, _) ->
                        viewModel.setLanguage(code)
                        showLangDialog = false
                        // Recreate activity to apply language changes
                        (context as? android.app.Activity)?.recreate()
                    },
                    onDismissRequest = { showLangDialog = false },
                    headerIcon = Icons.Default.Info,
                    labelProvider = { stringResource(id = it.second) },
                    iconProvider = { (_, _, flag) ->
                        Text(
                            text = flag,
                            fontSize = 24.sp,
                            modifier = Modifier.size(32.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                )
            }
        }
    }
}
