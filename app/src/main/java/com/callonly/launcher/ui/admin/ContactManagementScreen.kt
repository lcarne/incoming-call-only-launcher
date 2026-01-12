package com.callonly.launcher.ui.admin

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.callonly.launcher.R
import com.callonly.launcher.data.model.Contact
import com.callonly.launcher.ui.admin.components.ContactListItem
import com.callonly.launcher.ui.admin.dialogs.ContactDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactManagementScreen(
    viewModel: AdminViewModel,
    onBack: () -> Unit,
    onOpenCamera: ((android.net.Uri) -> Unit) -> Unit
) {
    val contacts by viewModel.contacts.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var contactToEdit by remember { mutableStateOf<Contact?>(null) }
    var contactToDelete by remember { mutableStateOf<Contact?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.contacts)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_contact)
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            items(contacts) { contact ->
                ContactListItem(
                    contact = contact,
                    onClick = { contactToEdit = contact },
                    onDelete = { contactToDelete = contact }
                )
            }
        }

        if (contactToDelete != null) {
            AlertDialog(
                onDismissRequest = { contactToDelete = null },
                title = { Text(stringResource(id = R.string.delete_contact)) },
                text = {
                    Text(
                        stringResource(
                            id = R.string.confirm_delete_contact,
                            contactToDelete?.name ?: ""
                        )
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            contactToDelete?.let { viewModel.deleteContact(it) }
                            contactToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(stringResource(id = R.string.delete))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { contactToDelete = null }) {
                        Text(stringResource(id = R.string.cancel))
                    }
                }
            )
        }

        if (showAddDialog) {
            ContactDialog(
                contactToEdit = null,
                onDismiss = { showAddDialog = false },
                onOpenCamera = { onCaptured ->
                    onOpenCamera { uri ->
                        onCaptured(uri)
                    }
                },
                onConfirm = { name, number, photoUri ->
                    viewModel.addContact(name, number, photoUri)
                    showAddDialog = false
                }
            )
        }

        if (contactToEdit != null) {
            ContactDialog(
                contactToEdit = contactToEdit,
                onDismiss = { contactToEdit = null },
                onOpenCamera = { onCaptured ->
                    onOpenCamera { uri ->
                        onCaptured(uri)
                    }
                },
                onConfirm = { name, number, photoUri ->
                    viewModel.updateContact(
                        contactToEdit!!.copy(
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
