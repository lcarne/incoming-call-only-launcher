package com.incomingcallonly.launcher.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.incomingcallonly.launcher.R

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
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    var showResetDataDialog by remember { androidx.compose.runtime.mutableStateOf(false) }
    var showResetSettingsDialog by remember { androidx.compose.runtime.mutableStateOf(false) }

    if (showResetDataDialog) {
        AlertDialog(
            onDismissRequest = { showResetDataDialog = false },
            title = { Text(stringResource(R.string.reset_all_data)) },
            text = { Text(stringResource(R.string.confirm_reset_all_data)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAllData()
                        showResetDataDialog = false
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDataDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showResetSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showResetSettingsDialog = false },
            title = { Text(stringResource(R.string.reset_settings)) },
            text = { Text(stringResource(R.string.confirm_reset_settings)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetSettings()
                        showResetSettingsDialog = false
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetSettingsDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

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
                        imageVector = Icons.Default.PlayArrow,
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

            HorizontalDivider()

            ListItem(
                headlineContent = { Text(stringResource(id = R.string.reset_all_data)) },
                leadingContent = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                modifier = Modifier.clickable { showResetDataDialog = true }
            )

            ListItem(
                headlineContent = { Text(stringResource(id = R.string.reset_settings)) },
                leadingContent = { Icon(Icons.Default.Refresh, contentDescription = null) },
                modifier = Modifier.clickable { showResetSettingsDialog = true }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Support Section
            Text(
                text = stringResource(id = R.string.support),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            ListItem(
                headlineContent = { Text(stringResource(id = R.string.buy_me_coffee)) },
                leadingContent = { Icon(Icons.Default.Favorite, contentDescription = null, tint = androidx.compose.ui.graphics.Color(0xFFFFDD00)) }, // BMC Yellow/Gold or just rely on text
                modifier = Modifier.clickable { uriHandler.openUri("https://buymeacoffee.com/leocarne") }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingsSection(viewModel: AdminViewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        com.incomingcallonly.launcher.ui.admin.components.SettingsSystemSection(viewModel)
        com.incomingcallonly.launcher.ui.admin.components.SettingsAudioSection(viewModel)
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        com.incomingcallonly.launcher.ui.admin.components.SettingsDisplaySection(viewModel)
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        com.incomingcallonly.launcher.ui.admin.components.SettingsLocalizationSection(viewModel)
    }
}
