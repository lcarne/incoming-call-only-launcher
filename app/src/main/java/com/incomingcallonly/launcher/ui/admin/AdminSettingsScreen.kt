package com.incomingcallonly.launcher.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.incomingcallonly.launcher.R
import com.incomingcallonly.launcher.ui.admin.components.AdminDangerButton
import com.incomingcallonly.launcher.ui.admin.components.AdminDialog
import com.incomingcallonly.launcher.ui.admin.components.AdminDivider
import com.incomingcallonly.launcher.ui.admin.components.AdminIcon
import com.incomingcallonly.launcher.ui.admin.components.AdminNavigationItem
import com.incomingcallonly.launcher.ui.admin.components.AdminSectionHeader
import com.incomingcallonly.launcher.ui.admin.components.AdminSettingsCard
import com.incomingcallonly.launcher.ui.admin.components.SettingsAudioSection
import com.incomingcallonly.launcher.ui.admin.components.SettingsDisplaySection
import com.incomingcallonly.launcher.ui.admin.components.SettingsLocalizationSection
import com.incomingcallonly.launcher.ui.admin.components.SettingsSystemSection
import com.incomingcallonly.launcher.ui.components.DepthIcon
import com.incomingcallonly.launcher.ui.theme.Spacing
import com.incomingcallonly.launcher.ui.theme.SystemBarsColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSettingsScreen(
    settingsViewModel: SettingsViewModel,
    contactsViewModel: ContactsViewModel,
    authViewModel: AuthViewModel,
    onExit: () -> Unit,
    onLogout: () -> Unit,
    onUnpin: () -> Unit,
    onManageContacts: () -> Unit,
    onShowHistory: () -> Unit
) {
    val scrollState = rememberScrollState()
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    var showResetDataDialog by remember { androidx.compose.runtime.mutableStateOf(false) }
    var showResetSettingsDialog by remember { androidx.compose.runtime.mutableStateOf(false) }

    // System Bars Configuration
    val isDarkTheme = isSystemInDarkTheme()
    SystemBarsColor(
        darkIcons = !isDarkTheme
    )

    // Modern Dialogs
    if (showResetDataDialog) {
        AdminDialog(
            onDismissRequest = { showResetDataDialog = false },
            title = stringResource(R.string.reset_all_data),
            icon = Icons.Default.Delete,
            iconContainerColor = MaterialTheme.colorScheme.errorContainer,
            iconTint = MaterialTheme.colorScheme.error,
            animated = false,
            content = {
                Text(
                    stringResource(R.string.confirm_reset_all_data),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error
                )
            },
            confirmButton = {
            AdminDangerButton(
                    text = stringResource(R.string.confirm),
                    onClick = {
                        settingsViewModel.deleteAllData()
                        showResetDataDialog = false
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showResetDataDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showResetSettingsDialog) {
        AdminDialog(
            onDismissRequest = { showResetSettingsDialog = false },
            title = stringResource(R.string.reset_settings),
            icon = Icons.Default.Refresh,
            animated = false,
            content = {
                Text(
                    stringResource(R.string.confirm_reset_settings),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            confirmButton = {
            AdminDangerButton( // Changed to DangerButton as requested
                    text = stringResource(R.string.confirm),
                    onClick = {
                        settingsViewModel.resetSettings()
                        showResetSettingsDialog = false
                    }
                )
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
            uri?.let { contactsViewModel.exportContacts(it) }
        }
    )

    val importLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let { contactsViewModel.importContacts(it) }
        }
    )

    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }
    val importExportState by contactsViewModel.importExportState.collectAsState()

    androidx.compose.runtime.LaunchedEffect(importExportState) {
        importExportState?.let { result ->
            when (result) {
                is Result.Success -> {
                    snackbarHostState.showSnackbar(result.data)
                    contactsViewModel.resetImportExportState()
                }
                is Result.Error -> {
                    snackbarHostState.showSnackbar(result.message ?: "Unknown error")
                    contactsViewModel.resetImportExportState()
                }
                is Result.Loading -> {
                     // Optionally show loading indicator
                }
            }
        }
    }

    Scaffold(
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { androidx.compose.material3.SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        stringResource(id = R.string.settings),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.25f),
                                offset = Offset(2f, 2f),
                                blurRadius = 4f
                            )
                        )
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.shadow(
                    elevation = 4.dp,
                    spotColor = Color.Black.copy(alpha = 0.15f)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Quick Actions - Now more prominent
            AdminSettingsCard {
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { 
                        Text(
                            stringResource(id = R.string.unlock),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        ) 
                    },
                    leadingContent = {
                        AdminIcon(
                            painter = painterResource(id = R.drawable.ic_lock_open),
                            tint = MaterialTheme.colorScheme.error,
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                        )
                    },
                    modifier = Modifier.clickable {
                        onUnpin()
                        onLogout()
                        onExit()
                    }
                )
                AdminDivider()
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { 
                        Text(
                            stringResource(id = R.string.back_arrow),
                            style = MaterialTheme.typography.titleMedium
                        ) 
                    },
                    leadingContent = {
                        AdminIcon(
                            painter = painterResource(id = R.drawable.ic_arrow_back)
                        )
                    },
                    modifier = Modifier.clickable {
                        onLogout()
                        onExit()
                    }
                )
            }

            // Content Management
            AdminSectionHeader(text = stringResource(id = R.string.settings_section_content))
            
            // Standalone buttons for Content Management (as requested)
            AdminNavigationItem(
                headlineText = stringResource(id = R.string.manage_contacts),
                leadingIcon = {
                    AdminIcon(
                        painter = painterResource(id = R.drawable.ic_list),
                        tint = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                },
                onClick = onManageContacts,
                trailingIcon = {
                    DepthIcon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(Spacing.iconSmall),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.padding(horizontal = Spacing.md)
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            AdminNavigationItem(
                headlineText = stringResource(id = R.string.call_history),
                leadingIcon = {
                    AdminIcon(
                        painter = painterResource(id = R.drawable.ic_call),
                        tint = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                },
                onClick = onShowHistory,
                trailingIcon = {
                    DepthIcon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(Spacing.iconSmall),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.padding(horizontal = Spacing.md)
            )

            // Settings Section
            SettingsSection(settingsViewModel, authViewModel)

            // Data Management
            AdminSectionHeader(text = stringResource(id = R.string.data_management))

            AdminSettingsCard {
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text(stringResource(id = R.string.export_contacts)) },
                    leadingContent = { 
                        AdminIcon(
                            imageVector = Icons.Default.Share,
                            tint = MaterialTheme.colorScheme.primary
                        ) 
                    },
                    modifier = Modifier.clickable { exportLauncher.launch("contacts_backup.json") }
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text(stringResource(id = R.string.import_contacts)) },
                    leadingContent = { 
                        AdminIcon(
                            imageVector = Icons.Default.Add,
                            tint = MaterialTheme.colorScheme.primary
                        ) 
                    },
                    modifier = Modifier.clickable { importLauncher.launch(arrayOf("application/json")) }
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            // Dangerous Zone - Same style as export/import section
            AdminSettingsCard {
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text(stringResource(id = R.string.reset_settings)) },
                    leadingContent = { 
                        AdminIcon(
                            imageVector = Icons.Default.Refresh,
                            tint = MaterialTheme.colorScheme.primary,
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ) 
                    },
                    modifier = Modifier.clickable { showResetSettingsDialog = true }
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { 
                        Text(
                            stringResource(id = R.string.reset_all_data),
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        ) 
                    },
                    leadingContent = { 
                        AdminIcon(
                            imageVector = Icons.Default.Delete,
                            tint = MaterialTheme.colorScheme.error,
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ) 
                    },
                    modifier = Modifier.clickable { showResetDataDialog = true }
                )
            }

            // Support Section
            AdminSectionHeader(text = stringResource(id = R.string.support))

            AdminSettingsCard {
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text(stringResource(id = R.string.buy_me_coffee)) },
                    leadingContent = { 
                        AdminIcon(
                            imageVector = Icons.Default.Favorite,
                            tint = Color(0xFFFFDD00), // Gold
                            containerColor = Color(0xFFFFDD00).copy(alpha = 0.1f)
                        ) 
                    },
                    trailingContent = {
                        DepthIcon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(Spacing.iconLarge),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    },
                    modifier = Modifier.clickable {
                        onUnpin()
                        uriHandler.openUri("https://buymeacoffee.com/leocarne") 
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing.xl))

            Text(
                text = stringResource(id = R.string.settings_footer_message),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.xl),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing.xxxl))
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingsSection(viewModel: SettingsViewModel, authViewModel: AuthViewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SettingsSystemSection(viewModel, authViewModel)
        SettingsAudioSection(viewModel)
        SettingsDisplaySection(viewModel)
        SettingsLocalizationSection(viewModel)
    }
}
