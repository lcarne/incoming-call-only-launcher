package com.incomingcallonly.launcher.ui.admin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.incomingcallonly.launcher.R
import com.incomingcallonly.launcher.data.model.Contact
import com.incomingcallonly.launcher.ui.admin.components.AdminDangerButton
import com.incomingcallonly.launcher.ui.admin.components.AdminDialog
import com.incomingcallonly.launcher.ui.admin.components.AdminLargeButton
import com.incomingcallonly.launcher.ui.admin.components.ContactListItem
import com.incomingcallonly.launcher.ui.admin.dialogs.ContactDialog
import com.incomingcallonly.launcher.ui.theme.Spacing
import com.incomingcallonly.launcher.ui.theme.SystemBarsColor
import com.incomingcallonly.launcher.ui.components.DepthIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactManagementScreen(
    viewModel: ContactsViewModel,
    onBack: () -> Unit,
    onOpenCamera: ((android.net.Uri) -> Unit) -> Unit
) {
    val contacts by viewModel.contacts.collectAsState()

    BackHandler(onBack = onBack)

    var showAddDialog by remember { mutableStateOf(false) }
    var contactToEdit by remember { mutableStateOf<Contact?>(null) }
    var contactToDelete by remember { mutableStateOf<Contact?>(null) }

    // System Bars Configuration - Transparent for edge-to-edge
    val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    SystemBarsColor(
        statusBarColor = Color.Transparent,
        navigationBarColor = Color.Transparent,
        darkIcons = !isDarkTheme
    )

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.contacts)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        DepthIcon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.shadow(
                    elevation = 4.dp,
                    spotColor = Color.Black.copy(alpha = 0.15f)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AdminLargeButton(
                text = stringResource(R.string.add_contact),
                icon = Icons.Default.Add,
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(
                    top = Spacing.md,
                    bottom = Spacing.xs
                )
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(contacts) { contact ->
                    ContactListItem(
                        contact = contact,
                        onClick = { contactToEdit = contact },
                        onDelete = { contactToDelete = contact }
                    )
                }
            }
        }

        contactToDelete?.let { contact ->
            AdminDialog(
                onDismissRequest = { contactToDelete = null },
                title = stringResource(R.string.delete_contact),
                icon = Icons.Default.Delete,
                iconContainerColor = MaterialTheme.colorScheme.errorContainer,
                iconTint = MaterialTheme.colorScheme.error,
                animated = false,
                content = {
                    Text(
                        text = stringResource(
                            R.string.confirm_delete_contact,
                            contact.name
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    AdminDangerButton(
                        text = stringResource(R.string.delete),
                        onClick = {
                            viewModel.deleteContact(contact)
                            contactToDelete = null
                        }
                    )
                },
                dismissButton = {
                    TextButton(onClick = { contactToDelete = null }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }

        if (showAddDialog) {
            ContactDialog(
                contactToEdit = null,
                onDismiss = { showAddDialog = false },
                onOpenCamera = onOpenCamera,
                onConfirm = { name, number, photoUri ->
                    viewModel.addContact(name, number, photoUri)
                    showAddDialog = false
                }
            )
        }

        contactToEdit?.let { contact ->
            ContactDialog(
                contactToEdit = contact,
                onDismiss = { contactToEdit = null },
                onOpenCamera = onOpenCamera,
                onConfirm = { name, number, photoUri ->
                    viewModel.updateContact(
                        contact.copy(
                            name = name,
                            phoneNumber = number,
                            photoUri = photoUri
                        )
                    )
                    contactToEdit = null
                }
            )
        }
    }
}
