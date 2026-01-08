package com.callonly.launcher.ui.admin

import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester

import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.callonly.launcher.data.model.Contact
import androidx.compose.material3.Switch
import androidx.compose.material3.Slider
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Divider
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.callonly.launcher.ui.theme.HighContrastButtonBg

@Composable
fun AdminScreen(
    onExit: () -> Unit,
    onUnpin: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()

    if (!isAuthenticated) {
        PinEntryScreen(
            viewModel = viewModel,
            onExit = onExit
        )
    } else {
        AdminContent(
            viewModel = viewModel,
            onExit = onExit,
            onUnpin = onUnpin
        )
    }
}

@Composable
fun PinEntryScreen(
    viewModel: AdminViewModel,
    onExit: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    val isError by viewModel.pinError.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(stringResource(id = com.callonly.launcher.R.string.admin_mode), style = MaterialTheme.typography.headlineMedium)
        
        Spacer(modifier = Modifier.height(32.dp))

        // PIN Display
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(4) { index ->
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp)
                        .background(
                            color = if (index < pin.length) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = CircleShape
                        )
                )
            }
        }
        
        if (isError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(stringResource(id = com.callonly.launcher.R.string.incorrect_pin), color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Custom Keypad
        val keys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("", "0", "DEL")
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            keys.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    row.forEach { key ->
                        if (key.isEmpty()) {
                            Spacer(modifier = Modifier.size(72.dp))
                        } else if (key == "DEL") {
                            IconButton(
                                onClick = {
                                    if (pin.isNotEmpty()) {
                                        pin = pin.dropLast(1)
                                    }
                                },
                                modifier = Modifier.size(72.dp)
                            ) {
                                Icon(
                                    imageVector = com.callonly.launcher.ui.theme.StatusIcons.ArrowBack, // Or appropriate delete icon
                                    contentDescription = "Delete"
                                )
                            }
                        } else {
                            Button(
                                onClick = {
                                    if (pin.length < 4) {
                                        pin += key
                                        if (pin.length == 4) {
                                            viewModel.verifyPin(pin)
                                            // Reset if error (optional, logic inside verifyPin updates error state)
                                            // But if successful, it moves to AdminContent. If failed, pin stays? 
                                            // Ideally clear pin on error? logic: verifyPin sets isError.
                                        }
                                    }
                                },
                                modifier = Modifier.size(72.dp),
                                shape = CircleShape
                            ) {
                                Text(key, style = MaterialTheme.typography.titleLarge)
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        TextButton(onClick = onExit) {
            Text(stringResource(id = com.callonly.launcher.R.string.back))
        }
    }
    
    // Clear pin on error change if needed, or keep it to let user fix.
    // If error, maybe shake or clear? For now basic.
    LaunchedEffect(isError) {
        if (isError) {
            kotlinx.coroutines.delay(500)
            pin = "" // Auto clear on error after delay
        }
    }
}

@Composable
fun AdminContent(
    viewModel: AdminViewModel,
    onExit: () -> Unit,
    onUnpin: () -> Unit
) {
    var currentView by remember { mutableStateOf("SETTINGS") } // SETTINGS, CONTACTS, or HISTORY
    var pendingPhotoCaptured: ((android.net.Uri) -> Unit)? by remember { mutableStateOf(null) }
    var isCameraOpen by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentView) {
            "CONTACTS" -> {
                ContactManagementScreen(
                    viewModel = viewModel,
                    onBack = { currentView = "SETTINGS" },
                    onOpenCamera = { onCaptured ->
                        pendingPhotoCaptured = onCaptured
                        isCameraOpen = true
                    }
                )
            }
            "HISTORY" -> {
                CallHistoryScreen(
                    viewModel = viewModel,
                    onBack = { currentView = "SETTINGS" }
                )
            }
            else -> {
                AdminSettingsScreen(
                    viewModel = viewModel,
                    onExit = onExit,
                    onUnpin = onUnpin,
                    onManageContacts = { currentView = "CONTACTS" },
                    onShowHistory = { currentView = "HISTORY" }
                )
            }
        }

        if (isCameraOpen) {
            androidx.compose.ui.window.Dialog(
                onDismissRequest = {
                    isCameraOpen = false
                    pendingPhotoCaptured = null
                },
                properties = androidx.compose.ui.window.DialogProperties(
                    usePlatformDefaultWidth = false,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = false
                )
            ) {
                CameraScreen(
                    onPhotoCaptured = { uri ->
                        pendingPhotoCaptured?.invoke(uri)
                        isCameraOpen = false
                        pendingPhotoCaptured = null
                    },
                    onCancel = {
                        isCameraOpen = false
                        pendingPhotoCaptured = null
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSettingsScreen(
    viewModel: AdminViewModel,
    onExit: () -> Unit,
    onUnpin: () -> Unit,
    onManageContacts: () -> Unit,
    onShowHistory: () -> Unit
) {
    val scrollState = androidx.compose.foundation.rememberScrollState()
    
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
                title = { Text(stringResource(id = com.callonly.launcher.R.string.settings)) }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = com.callonly.launcher.R.string.settings),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {
                            onUnpin()
                            viewModel.logout()
                            onExit()
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)
                    ) {
                        Icon(com.callonly.launcher.ui.theme.StatusIcons.LockOpen, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(id = com.callonly.launcher.R.string.unlock))
                    }
                    
                    Button(
                        onClick = {
                            viewModel.logout()
                            onExit()
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Icon(com.callonly.launcher.ui.theme.StatusIcons.ArrowBack, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(id = com.callonly.launcher.R.string.back_arrow))
                    }
                }

                Button(
                    onClick = onManageContacts,
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                     colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                     Icon(com.callonly.launcher.ui.theme.StatusIcons.List, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                     Spacer(modifier = Modifier.width(16.dp))
                     Text(stringResource(id = com.callonly.launcher.R.string.manage_contacts), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onShowHistory,
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Icon(com.callonly.launcher.ui.theme.StatusIcons.Call, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(stringResource(id = com.callonly.launcher.R.string.call_history), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSecondaryContainer)
                }
                
                SettingsSection(viewModel)

                // Data Management Section
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(id = com.callonly.launcher.R.string.data_management), style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { exportLauncher.launch("contacts_backup.json") },
                    modifier = Modifier.fillMaxWidth().height(64.dp)
                ) {
                    Text(stringResource(id = com.callonly.launcher.R.string.export_contacts), textAlign = TextAlign.Center)
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { importLauncher.launch(arrayOf("application/json")) },
                    modifier = Modifier.fillMaxWidth().height(64.dp)
                ) {
                    Text(stringResource(id = com.callonly.launcher.R.string.import_contacts), textAlign = TextAlign.Center)
                }

                Spacer(modifier = Modifier.height(88.dp))
            }
        }
    }
}

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
                title = { Text(stringResource(id = com.callonly.launcher.R.string.contacts)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(com.callonly.launcher.ui.theme.StatusIcons.ArrowBack, contentDescription = stringResource(id = com.callonly.launcher.R.string.back))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(id = com.callonly.launcher.R.string.add_contact))
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
                title = { Text(stringResource(id = com.callonly.launcher.R.string.delete_contact)) },
                text = { Text(stringResource(id = com.callonly.launcher.R.string.confirm_delete_contact, contactToDelete?.name ?: "")) },
                confirmButton = {
                    Button(
                        onClick = {
                            contactToDelete?.let { viewModel.deleteContact(it) }
                            contactToDelete = null
                        },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(stringResource(id = com.callonly.launcher.R.string.delete))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { contactToDelete = null }) {
                        Text(stringResource(id = com.callonly.launcher.R.string.cancel))
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
                    viewModel.updateContact(contactToEdit!!.copy(name = name, phoneNumber = number, photoUri = photoUri))
                    contactToEdit = null
                }
            )
        }
    }
}

@Composable
fun ContactListItem(
    contact: Contact,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Tiny preview
            if (contact.photoUri != null) {
                AsyncImage(
                    model = contact.photoUri,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Column {
                Text(contact.name, style = MaterialTheme.typography.titleLarge)
                Text(contact.phoneNumber, style = MaterialTheme.typography.bodyMedium)
            }
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = stringResource(id = com.callonly.launcher.R.string.delete))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDialog(
    contactToEdit: Contact?,
    onDismiss: () -> Unit,
    onOpenCamera: ((android.net.Uri) -> Unit) -> Unit,
    onConfirm: (String, String, String?) -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<android.net.Uri?>(null) }
    
    // Pre-fill if editing
    androidx.compose.runtime.LaunchedEffect(contactToEdit) {
        if (contactToEdit != null) {
            val names = contactToEdit.name.split(" ")
            if (names.isNotEmpty()) {
                firstName = names.first()
                lastName = if (names.size > 1) names.drop(1).joinToString(" ") else ""
            } else {
                firstName = contactToEdit.name
            }
            number = contactToEdit.phoneNumber
            if (contactToEdit.photoUri != null) {
                photoUri = android.net.Uri.parse(contactToEdit.photoUri)
            }
        }
    }
    
    val context = androidx.compose.ui.platform.LocalContext.current
    
    val photoPicker = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> photoUri = uri }
    )

    var showPhotoSourceDialog by remember { mutableStateOf(false) }

    val contactPicker = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.PickContact(),
        onResult = { contactUri ->
            if (contactUri != null) {
                // Query Name
                val cursor = context.contentResolver.query(contactUri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val nameIndex = it.getColumnIndex(android.provider.ContactsContract.Contacts.DISPLAY_NAME)
                        val idIndex = it.getColumnIndex(android.provider.ContactsContract.Contacts._ID)
                        val photoIndex = it.getColumnIndex(android.provider.ContactsContract.Contacts.PHOTO_URI)
                        
                        if (nameIndex != -1) {
                            val fullName = it.getString(nameIndex) ?: ""
                            val names = fullName.split(" ")
                            if (names.isNotEmpty()) {
                                firstName = names.first()
                                lastName = if (names.size > 1) names.drop(1).joinToString(" ") else ""
                            } else {
                                firstName = fullName
                            }
                        }
                        
                        if (photoIndex != -1) {
                            val uriStr = it.getString(photoIndex)
                            if (uriStr != null) {
                                photoUri = android.net.Uri.parse(uriStr)
                            }
                        }
                        
                        // Query Phone Number
                        if (idIndex != -1) {
                            val contactId = it.getString(idIndex)
                            val phoneCursor = context.contentResolver.query(
                                android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                arrayOf(contactId),
                                null
                            )
                            phoneCursor?.use { pc ->
                                if (pc.moveToFirst()) {
                                    val numberIndex = pc.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER)
                                    if (numberIndex != -1) {
                                        number = pc.getString(numberIndex) ?: ""
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )

    val cameraPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // If granted, the user will have to click again. 
                // Or we could trigger it here if we store state.
                // Keeping it simple for now: user clicks again.
            }
        }
    )

    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                contactPicker.launch(null)
            }
        }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Column {
                Text(if (contactToEdit == null) stringResource(id = com.callonly.launcher.R.string.new_contact) else stringResource(id = com.callonly.launcher.R.string.edit_contact))
                if (contactToEdit == null) {
                    TextButton(
                        onClick = { 
                            val permission = android.Manifest.permission.READ_CONTACTS
                            if (androidx.core.content.ContextCompat.checkSelfPermission(context, permission) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                contactPicker.launch(null)
                            } else {
                                permissionLauncher.launch(permission)
                            }
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Icon(androidx.compose.material.icons.Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(id = com.callonly.launcher.R.string.import_from_directory))
                    }
                }
            }
        },
        text = {
            Column {
                val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
                
                Text(stringResource(id = com.callonly.launcher.R.string.first_name), style = MaterialTheme.typography.labelLarge)
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = androidx.compose.ui.text.input.ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
                    ),
                    modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()
                )
                Text(stringResource(id = com.callonly.launcher.R.string.last_name), style = MaterialTheme.typography.labelLarge)
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = androidx.compose.ui.text.input.ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
                    ),
                    modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()
                )
                Text(stringResource(id = com.callonly.launcher.R.string.number), style = MaterialTheme.typography.labelLarge)
                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = androidx.compose.ui.text.input.ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()
                )
                Button(
                    onClick = { showPhotoSourceDialog = true },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    val hasPhoto = photoUri != null
                    Icon(
                        if (hasPhoto) androidx.compose.material.icons.Icons.Default.Check else androidx.compose.material.icons.Icons.Default.Add,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (hasPhoto) stringResource(id = com.callonly.launcher.R.string.photo_selected) else stringResource(id = com.callonly.launcher.R.string.add_photo))
                }
                
                if (showPhotoSourceDialog) {
                    AlertDialog(
                        onDismissRequest = { showPhotoSourceDialog = false },
                        title = { Text(stringResource(id = com.callonly.launcher.R.string.photo_source)) },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        showPhotoSourceDialog = false
                                        photoPicker.launch(
                                            androidx.activity.result.PickVisualMediaRequest(
                                                androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
                                            )
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(com.callonly.launcher.ui.theme.StatusIcons.PhotoLibrary, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(stringResource(id = com.callonly.launcher.R.string.gallery))
                                }
                                Button(
                                    onClick = {
                                        showPhotoSourceDialog = false
                                        val permission = android.Manifest.permission.CAMERA
                                        if (androidx.core.content.ContextCompat.checkSelfPermission(context, permission) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                            onOpenCamera { uri ->
                                                photoUri = uri
                                            }
                                        } else {
                                            cameraPermissionLauncher.launch(permission)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(com.callonly.launcher.ui.theme.StatusIcons.PhotoCamera, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(stringResource(id = com.callonly.launcher.R.string.camera))
                                }
                            }
                        },
                        confirmButton = {},
                        dismissButton = {
                            TextButton(onClick = { showPhotoSourceDialog = false }) {
                                Text(stringResource(id = com.callonly.launcher.R.string.cancel))
                            }
                        }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    if (firstName.isNotBlank() && number.isNotBlank()) {
                         val fullName = if (lastName.isNotBlank()) "$firstName $lastName" else firstName
                         onConfirm(fullName, number, photoUri?.toString()) 
                    }
                }
            ) {
                Text(if (contactToEdit == null) stringResource(id = com.callonly.launcher.R.string.add) else stringResource(id = com.callonly.launcher.R.string.edit))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = com.callonly.launcher.R.string.cancel))
            }
        }
    )
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun SettingsSection(viewModel: AdminViewModel) {
    val isNightModeEnabled by viewModel.isNightModeEnabled.collectAsState()
    val nightStart by viewModel.nightModeStartHour.collectAsState()
    val nightStartMin by viewModel.nightModeStartMinute.collectAsState()
    val nightEnd by viewModel.nightModeEndHour.collectAsState()
    val nightEndMin by viewModel.nightModeEndMinute.collectAsState()


    Column(modifier = Modifier.padding(16.dp)) {
        Divider(modifier = Modifier.padding(vertical = 16.dp))
        Text(stringResource(id = com.callonly.launcher.R.string.call_security), style = MaterialTheme.typography.titleLarge)
        
        val allowAllCalls by viewModel.allowAllCalls.collectAsState()
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
                Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(id = com.callonly.launcher.R.string.accept_all_calls), style = MaterialTheme.typography.titleMedium)
                Text(stringResource(id = com.callonly.launcher.R.string.accept_all_calls_desc), style = MaterialTheme.typography.bodySmall)
            }
            Switch(
                checked = allowAllCalls,
                onCheckedChange = { viewModel.setAllowAllCalls(it) }
            )
        }

        // Language selection removed from here; use bottom Langue button in AdminSettingsScreen

        Spacer(modifier = Modifier.height(16.dp))

        // Default Audio Output Setting
        Text(stringResource(id = com.callonly.launcher.R.string.default_audio_output), style = MaterialTheme.typography.titleMedium)
        val isDefaultSpeakerEnabled by viewModel.isDefaultSpeakerEnabled.collectAsState()
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.setDefaultSpeakerEnabled(false) },
                modifier = Modifier.weight(1f).defaultMinSize(minHeight = 56.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = if (!isDefaultSpeakerEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (!isDefaultSpeakerEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Icon(com.callonly.launcher.ui.theme.StatusIcons.Hearing, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(id = com.callonly.launcher.R.string.earpiece),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = { viewModel.setDefaultSpeakerEnabled(true) },
                modifier = Modifier.weight(1f).defaultMinSize(minHeight = 56.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = if (isDefaultSpeakerEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (isDefaultSpeakerEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Icon(com.callonly.launcher.ui.theme.StatusIcons.Speaker, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(id = com.callonly.launcher.R.string.speaker),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        val ringerVolume by viewModel.ringerVolume.collectAsState()
        Text(stringResource(id = com.callonly.launcher.R.string.ringer_volume), style = MaterialTheme.typography.titleMedium)
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${ringerVolume}%", style = MaterialTheme.typography.bodyMedium)
            
            IconButton(
                onClick = { viewModel.testRingtone() },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = stringResource(id = com.callonly.launcher.R.string.test_ringer),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Slider(
            value = ringerVolume.toFloat(),
            onValueChange = { viewModel.setRingerVolume(it.toInt()) },
            valueRange = 0f..100f
        )
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(id = com.callonly.launcher.R.string.ringer_active), style = MaterialTheme.typography.titleMedium)
            }
            Switch(
                checked = viewModel.isRingerEnabled.collectAsState().value,
                onCheckedChange = { viewModel.setRingerEnabled(it) }
            )
        }
        Divider(modifier = Modifier.padding(vertical = 16.dp))
        Text(stringResource(id = com.callonly.launcher.R.string.screen_settings), style = MaterialTheme.typography.titleLarge)
        
        val screenBehaviorPlugged by viewModel.screenBehaviorPlugged.collectAsState()
        val screenBehaviorBattery by viewModel.screenBehaviorBattery.collectAsState()

        var showPluggedDialog by remember { mutableStateOf(false) }
        var showBatteryDialog by remember { mutableStateOf(false) }

        // Plugged
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Icon(com.callonly.launcher.ui.theme.StatusIcons.Charging, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(id = com.callonly.launcher.R.string.screen_behavior_plugged), style = MaterialTheme.typography.titleMedium)
            }
            Button(onClick = { showPluggedDialog = true }) {
                Text(
                    text = when(screenBehaviorPlugged) {
                        0 -> stringResource(id = com.callonly.launcher.R.string.mode_off)
                        1 -> stringResource(id = com.callonly.launcher.R.string.mode_dim)
                        2 -> stringResource(id = com.callonly.launcher.R.string.mode_awake)
                        else -> ""
                    }
                )
            }
        }
        
        // Battery
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Icon(com.callonly.launcher.ui.theme.StatusIcons.BatteryFull, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(id = com.callonly.launcher.R.string.screen_behavior_battery), style = MaterialTheme.typography.titleMedium)
            }
            Button(onClick = { showBatteryDialog = true }) {
                Text(
                    text = when(screenBehaviorBattery) {
                        0 -> stringResource(id = com.callonly.launcher.R.string.mode_off)
                        1 -> stringResource(id = com.callonly.launcher.R.string.mode_dim)
                        2 -> stringResource(id = com.callonly.launcher.R.string.mode_awake)
                        else -> ""
                    }
                )
            }
        }

        if (showPluggedDialog) {
             ScreenBehaviorDialog(
                 title = stringResource(id = com.callonly.launcher.R.string.screen_behavior_plugged),
                 icon = com.callonly.launcher.ui.theme.StatusIcons.Charging,
                 currentValue = screenBehaviorPlugged,
                 onConfirm = { viewModel.setScreenBehaviorPlugged(it); showPluggedDialog = false },
                 onDismiss = { showPluggedDialog = false }
             )
        }

        if (showBatteryDialog) {
             ScreenBehaviorDialog(
                 title = stringResource(id = com.callonly.launcher.R.string.screen_behavior_battery),
                 icon = com.callonly.launcher.ui.theme.StatusIcons.BatteryFull,
                 currentValue = screenBehaviorBattery,
                 onConfirm = { viewModel.setScreenBehaviorBattery(it); showBatteryDialog = false },
                 onDismiss = { showBatteryDialog = false }
             )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(id = com.callonly.launcher.R.string.night_mode), style = MaterialTheme.typography.titleMedium)
                Text(stringResource(id = com.callonly.launcher.R.string.night_mode_desc), style = MaterialTheme.typography.bodySmall)
            }
            Switch(
                checked = isNightModeEnabled,
                onCheckedChange = { viewModel.setNightModeEnabled(it) }
            )
        }



        if (isNightModeEnabled) {
            var showStartPicker by remember { mutableStateOf(false) }
            var showEndPicker by remember { mutableStateOf(false) }

            Spacer(modifier = Modifier.height(8.dp))
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                // Start Time
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showStartPicker = true }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(id = com.callonly.launcher.R.string.night_start_label), style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = String.format("%02dh%02d", nightStart, nightStartMin),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Divider(modifier = Modifier.padding(horizontal = 12.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

                // End Time
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showEndPicker = true }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(id = com.callonly.launcher.R.string.night_end_label), style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = String.format("%02dh%02d", nightEnd, nightEndMin),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Calculation and explanation
            val startTotalMinutes = nightStart * 60 + nightStartMin
            val endTotalMinutes = nightEnd * 60 + nightEndMin
            val durationMinutes = if (endTotalMinutes > startTotalMinutes) endTotalMinutes - startTotalMinutes else (24 * 60 - startTotalMinutes) + endTotalMinutes
            val durationHours = durationMinutes / 60
            val durationMinsOnly = durationMinutes % 60
            val nextDay = if (endTotalMinutes <= startTotalMinutes) stringResource(id = com.callonly.launcher.R.string.next_day) else ""
            
            Text(
                text = stringResource(id = com.callonly.launcher.R.string.night_mode_duration_desc, durationHours, if (durationMinsOnly > 0) String.format("%02d", durationMinsOnly) else "", nextDay),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp)
            )

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
        
        Divider(modifier = Modifier.padding(vertical = 16.dp))
        Text(stringResource(id = com.callonly.launcher.R.string.clock_color), style = MaterialTheme.typography.titleLarge)
        
        val colors = listOf(
            HighContrastButtonBg, // Default Blue/Yellow
            Color.White,
            Color.Cyan,
            Color.Green,
            Color.Magenta,
            Color.Red,
            Color.Yellow
        )
        val currentColor = if (viewModel.clockColor.collectAsState().value != 0) Color(viewModel.clockColor.collectAsState().value).toArgb() else HighContrastButtonBg.toArgb()

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
        ) {
            colors.forEach { color ->
                val isSelected = color.toArgb() == currentColor
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = 2.dp,
                            color = if (color == Color.White) Color.LightGray else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { viewModel.setClockColor(color.toArgb()) }
                        .border(
                            width = if (isSelected) 4.dp else 0.dp,
                            color = if (isSelected) {
                                if (color == Color.White) MaterialTheme.colorScheme.primary else Color.White
                            } else Color.Transparent,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(if (color == Color.White) MaterialTheme.colorScheme.primary else Color.White)
                        )
                    }
                }
            }
        }
        
        var showLangDialog by remember { mutableStateOf(false) }
        
        Spacer(modifier = Modifier.height(16.dp))
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        // Language section
        Text(
            text = stringResource(id = com.callonly.launcher.R.string.settings_language_section_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Button(
            onClick = { showLangDialog = true },
            modifier = Modifier.fillMaxWidth().height(64.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("文", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("A", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(stringResource(id = com.callonly.launcher.R.string.language), style = MaterialTheme.typography.titleLarge)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        // Time format selection (AM/PM vs 24h)
        val currentFormat by viewModel.timeFormat.collectAsState()
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Text(stringResource(id = com.callonly.launcher.R.string.time_format_title), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material3.RadioButton(
                    selected = currentFormat == "12",
                    onClick = { viewModel.setTimeFormat("12") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(id = com.callonly.launcher.R.string.time_format_12), modifier = Modifier.clickable { viewModel.setTimeFormat("12") })
                Spacer(modifier = Modifier.width(24.dp))
                androidx.compose.material3.RadioButton(
                    selected = currentFormat == "24",
                    onClick = { viewModel.setTimeFormat("24") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(id = com.callonly.launcher.R.string.time_format_24), modifier = Modifier.clickable { viewModel.setTimeFormat("24") })
            }
        }

        if (showLangDialog) {
            val lang by viewModel.language.collectAsState()
            val context = androidx.compose.ui.platform.LocalContext.current
            AlertDialog(
                onDismissRequest = { showLangDialog = false },
                title = { Text(stringResource(id = com.callonly.launcher.R.string.language)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.setLanguage("fr")
                                showLangDialog = false
                                (context as? android.app.Activity)?.recreate()
                            }
                            .padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("🇫🇷", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(stringResource(id = com.callonly.launcher.R.string.language_french), style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.weight(1f))
                            if (lang == "fr") Text("✓")
                        }

                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.setLanguage("en")
                                showLangDialog = false
                                (context as? android.app.Activity)?.recreate()
                            }
                            .padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("🇬🇧", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(stringResource(id = com.callonly.launcher.R.string.language_english), style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.weight(1f))
                            if (lang == "en") Text("✓")
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showLangDialog = false }) { Text(stringResource(id = com.callonly.launcher.R.string.cancel)) }
                }
            )
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialogWrapper(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val timePickerState = androidx.compose.material3.rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = { onConfirm(timePickerState.hour, timePickerState.minute) }) {
                androidx.compose.material3.Text(androidx.compose.ui.res.stringResource(id = com.callonly.launcher.R.string.confirm))
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                androidx.compose.material3.Text(androidx.compose.ui.res.stringResource(id = com.callonly.launcher.R.string.cancel))
            }
        },
        title = { androidx.compose.material3.Text(androidx.compose.ui.res.stringResource(id = com.callonly.launcher.R.string.select_hour)) },
        text = {
            androidx.compose.foundation.layout.Box(modifier = androidx.compose.ui.Modifier.fillMaxWidth(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                androidx.compose.material3.TimePicker(state = timePickerState)
            }
        }
    )
}

@Composable
fun ScreenBehaviorDialog(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    currentValue: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(icon, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                androidx.compose.material3.Text(title)
            }
        },
        text = {
            Column {
                // Off
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onConfirm(0) }
                        .padding(12.dp),
                     verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.RadioButton(
                        selected = currentValue == 0,
                        onClick = { onConfirm(0) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    androidx.compose.material3.Text(stringResource(id = com.callonly.launcher.R.string.mode_off))
                }
                // Dim
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onConfirm(1) }
                        .padding(12.dp),
                     verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.RadioButton(
                        selected = currentValue == 1,
                        onClick = { onConfirm(1) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    androidx.compose.material3.Text(stringResource(id = com.callonly.launcher.R.string.mode_dim))
                }
                // Awake
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onConfirm(2) }
                        .padding(12.dp),
                     verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.RadioButton(
                        selected = currentValue == 2,
                        onClick = { onConfirm(2) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    androidx.compose.material3.Text(stringResource(id = com.callonly.launcher.R.string.mode_awake))
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                androidx.compose.material3.Text(stringResource(id = com.callonly.launcher.R.string.cancel))
            }
        }
    )
}
