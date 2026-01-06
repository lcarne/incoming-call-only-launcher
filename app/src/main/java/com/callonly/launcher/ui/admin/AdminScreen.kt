package com.callonly.launcher.ui.admin

import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.callonly.launcher.data.model.Contact
import androidx.compose.material3.Switch
import androidx.compose.material3.Slider
import java.text.SimpleDateFormat // For displaying hours sensibly if needed, or just Int
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
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
                        Text(stringResource(id = com.callonly.launcher.R.string.unlock))
                    }
                    
                    Button(
                        onClick = {
                            viewModel.logout()
                            onExit()
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
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
                    Text(if (photoUri != null) stringResource(id = com.callonly.launcher.R.string.photo_selected) else stringResource(id = com.callonly.launcher.R.string.add_photo))
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
    val isAlwaysOn by viewModel.isAlwaysOnEnabled.collectAsState()
    val nightStart by viewModel.nightModeStartHour.collectAsState()
    val nightEnd by viewModel.nightModeEndHour.collectAsState()

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
        
        val ringerVolume by viewModel.ringerVolume.collectAsState()
        Text(stringResource(id = com.callonly.launcher.R.string.ringer_volume), style = MaterialTheme.typography.titleMedium)
        Text("${ringerVolume}%", style = MaterialTheme.typography.bodyMedium)
        Slider(
            value = ringerVolume.toFloat(),
            onValueChange = { viewModel.setRingerVolume(it.toInt()) },
            valueRange = 0f..100f,
            steps = 100
        )

        Spacer(modifier = Modifier.height(16.dp))
        
        val isVibrateEnabled by viewModel.isVibrateEnabled.collectAsState()
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
                Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(id = com.callonly.launcher.R.string.vibrate), style = MaterialTheme.typography.titleMedium)
                Text(stringResource(id = com.callonly.launcher.R.string.vibrate_desc), style = MaterialTheme.typography.bodySmall)
            }
            Switch(
                checked = isVibrateEnabled,
                onCheckedChange = { viewModel.setVibrateEnabled(it) }
            )
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))
        Text(stringResource(id = com.callonly.launcher.R.string.screen_settings), style = MaterialTheme.typography.titleLarge)
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
                Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(id = com.callonly.launcher.R.string.always_on_screen), style = MaterialTheme.typography.titleMedium)
                Text(stringResource(id = com.callonly.launcher.R.string.always_on_desc), style = MaterialTheme.typography.bodySmall)
            }
            Switch(
                checked = isAlwaysOn,
                onCheckedChange = { viewModel.setAlwaysOnEnabled(it) }
            )
        }

        if (isAlwaysOn) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(id = com.callonly.launcher.R.string.night_mode), style = MaterialTheme.typography.titleMedium)
            
            Text(stringResource(id = com.callonly.launcher.R.string.night_start, nightStart), style = MaterialTheme.typography.bodyMedium)
            Slider(
                value = nightStart.toFloat(),
                onValueChange = { viewModel.setNightModeStartHour(it.toInt()) },
                valueRange = 0f..23f,
                steps = 22
            )
            
            Text(stringResource(id = com.callonly.launcher.R.string.night_end, nightEnd), style = MaterialTheme.typography.bodyMedium)
            Slider(
                value = nightEnd.toFloat(),
                onValueChange = { viewModel.setNightModeEndHour(it.toInt()) },
                valueRange = 0f..23f,
                steps = 22
            )
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
        
        Divider(modifier = Modifier.padding(vertical = 16.dp))
        Text(stringResource(id = com.callonly.launcher.R.string.answer_button_size), style = MaterialTheme.typography.titleLarge)
        
        val answerButtonSize by viewModel.answerButtonSize.collectAsState()
        Text(stringResource(id = com.callonly.launcher.R.string.answer_button_size_value, answerButtonSize.toInt()), style = MaterialTheme.typography.bodyMedium)
        Slider(
            value = answerButtonSize,
            onValueChange = { viewModel.setAnswerButtonSize(it) },
            valueRange = 80f..200f,
            steps = 120
        )

        Spacer(modifier = Modifier.height(24.dp))
        
        var showPreview by remember { mutableStateOf(false) }
        var showLangDialog by remember { mutableStateOf(false) }
        
        Button(
            onClick = { showPreview = true },
            modifier = Modifier.fillMaxWidth().height(72.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer, contentColor = MaterialTheme.colorScheme.onTertiaryContainer)
        ) {
            Text(
                text = stringResource(id = com.callonly.launcher.R.string.preview_call_screen),
                maxLines = 2,
                softWrap = true,
                textAlign = TextAlign.Center
            )
        }

        if (showPreview) {
            androidx.compose.ui.window.Dialog(
                onDismissRequest = { showPreview = false },
                properties = androidx.compose.ui.window.DialogProperties(
                    usePlatformDefaultWidth = false,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .clickable { showPreview = false }
                ) {
                    com.callonly.launcher.ui.call.CallLayout(
                        number = "06 12 34 56 78",
                        contact = Contact(name = stringResource(id = com.callonly.launcher.R.string.preview_contact_name), phoneNumber = "06 12 34 56 78", photoUri = null),
                        state = com.callonly.launcher.ui.call.IncomingCallUiState.Ringing("06 12 34 56 78", null),
                        duration = 0,
                        answerButtonSize = answerButtonSize
                    )
                    
                    Text(
                        text = stringResource(id = com.callonly.launcher.R.string.click_anywhere_close),
                        color = Color.Gray.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        // Language section under preview
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
