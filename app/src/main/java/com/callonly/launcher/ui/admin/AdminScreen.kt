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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ListItem
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.callonly.launcher.data.model.Contact
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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header with Icon
            Icon(
                painter = androidx.compose.ui.res.painterResource(id = com.callonly.launcher.R.drawable.ic_lock_open),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                stringResource(id = com.callonly.launcher.R.string.admin_mode),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(48.dp))

            // PIN Display with modern dots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(4) { index ->
                    val isActive = index < pin.length
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(16.dp)
                            .background(
                                color = if (isActive) MaterialTheme.colorScheme.primary 
                                        else MaterialTheme.colorScheme.outlineVariant,
                                shape = CircleShape
                            )
                            .border(
                                width = if (isActive) 0.dp else 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = CircleShape
                            )
                    )
                }
            }

            Box(modifier = Modifier.height(40.dp)) {
                if (isError) {
                    Text(
                        stringResource(id = com.callonly.launcher.R.string.incorrect_pin),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Modern Material 3 Keypad
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
                            when (key) {
                                "" -> {
                                    Spacer(modifier = Modifier.size(72.dp))
                                }
                                "DEL" -> {
                                    androidx.compose.material3.FilledTonalIconButton(
                                        onClick = {
                                            if (pin.isNotEmpty()) {
                                                pin = pin.dropLast(1)
                                            }
                                        },
                                        modifier = Modifier.size(72.dp)
                                    ) {
                                        Icon(
                                            painter = androidx.compose.ui.res.painterResource(id = com.callonly.launcher.R.drawable.ic_arrow_back),
                                            contentDescription = "Delete"
                                        )
                                    }
                                }
                                else -> {
                                    androidx.compose.material3.OutlinedButton(
                                        onClick = {
                                            if (pin.length < 4) {
                                                pin += key
                                                if (pin.length == 4) {
                                                    viewModel.verifyPin(pin)
                                                }
                                            }
                                        },
                                        modifier = Modifier.size(72.dp),
                                        shape = CircleShape,
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                                    ) {
                                        Text(
                                            text = key,
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            TextButton(
                onClick = onExit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(id = com.callonly.launcher.R.string.back),
                    style = MaterialTheme.typography.labelLarge
                )
            }
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
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // General Actions
            ListItem(
                headlineContent = { Text(stringResource(id = com.callonly.launcher.R.string.unlock)) },
                leadingContent = {
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(id = com.callonly.launcher.R.drawable.ic_lock_open),
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
                headlineContent = { Text(stringResource(id = com.callonly.launcher.R.string.back_arrow)) },
                leadingContent = {
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(id = com.callonly.launcher.R.drawable.ic_arrow_back),
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
                text = stringResource(id = com.callonly.launcher.R.string.settings_section_content),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            ListItem(
                headlineContent = { Text(stringResource(id = com.callonly.launcher.R.string.manage_contacts)) },
                leadingContent = {
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(id = com.callonly.launcher.R.drawable.ic_list),
                        contentDescription = null
                    )
                },
                trailingContent = {
                     Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.PlayArrow, // Using PlayArrow as generic forward/action indicator for now or null
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                     )
                },
                modifier = Modifier.clickable(onClick = onManageContacts)
            )

            ListItem(
                headlineContent = { Text(stringResource(id = com.callonly.launcher.R.string.call_history)) },
                leadingContent = {
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(id = com.callonly.launcher.R.drawable.ic_call),
                        contentDescription = null
                    )
                },
                trailingContent = {
                     Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.PlayArrow,
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
                text = stringResource(id = com.callonly.launcher.R.string.data_management),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            ListItem(
                headlineContent = { Text(stringResource(id = com.callonly.launcher.R.string.export_contacts)) },
                leadingContent = { Icon(androidx.compose.material.icons.Icons.Default.Share, contentDescription = null) },
                modifier = Modifier.clickable { exportLauncher.launch("contacts_backup.json") }
            )

            ListItem(
                headlineContent = { Text(stringResource(id = com.callonly.launcher.R.string.import_contacts)) },
                leadingContent = { Icon(androidx.compose.material.icons.Icons.Default.Add, contentDescription = null) },
                modifier = Modifier.clickable { importLauncher.launch(arrayOf("application/json")) }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
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
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(id = com.callonly.launcher.R.drawable.ic_arrow_back),
                            contentDescription = stringResource(id = com.callonly.launcher.R.string.back)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(id = com.callonly.launcher.R.string.add_contact)
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
                title = { Text(stringResource(id = com.callonly.launcher.R.string.delete_contact)) },
                text = {
                    Text(
                        stringResource(
                            id = com.callonly.launcher.R.string.confirm_delete_contact,
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
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListItem(
    contact: Contact,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    ListItem(
        headlineContent = { Text(contact.name) },
        supportingContent = { Text(contact.phoneNumber) },
        leadingContent = {
            if (contact.photoUri != null) {
                AsyncImage(
                    model = contact.photoUri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        trailingContent = {
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(id = com.callonly.launcher.R.string.delete)
                )
            }
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
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
                        val nameIndex =
                            it.getColumnIndex(android.provider.ContactsContract.Contacts.DISPLAY_NAME)
                        val idIndex =
                            it.getColumnIndex(android.provider.ContactsContract.Contacts._ID)
                        val photoIndex =
                            it.getColumnIndex(android.provider.ContactsContract.Contacts.PHOTO_URI)

                        if (nameIndex != -1) {
                            val fullName = it.getString(nameIndex) ?: ""
                            val names = fullName.split(" ")
                            if (names.isNotEmpty()) {
                                firstName = names.first()
                                lastName =
                                    if (names.size > 1) names.drop(1).joinToString(" ") else ""
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
                                    val numberIndex =
                                        pc.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER)
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
            Text(
                text = if (contactToEdit == null) stringResource(id = com.callonly.launcher.R.string.new_contact) else stringResource(
                    id = com.callonly.launcher.R.string.edit_contact
                ),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(androidx.compose.foundation.rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val focusManager = androidx.compose.ui.platform.LocalFocusManager.current

                // Compact Import Button
                if (contactToEdit == null) {
                    androidx.compose.material3.SuggestionChip(
                        onClick = {
                            val permission = android.Manifest.permission.READ_CONTACTS
                            if (androidx.core.content.ContextCompat.checkSelfPermission(
                                    context,
                                    permission
                                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                            ) {
                                contactPicker.launch(null)
                            } else {
                                permissionLauncher.launch(permission)
                            }
                        },
                        label = { 
                            Text(
                                stringResource(id = com.callonly.launcher.R.string.import_from_directory),
                                style = MaterialTheme.typography.labelMedium
                            ) 
                        },
                        icon = { Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                // Compact Photo Selection
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val hasPhoto = photoUri != null
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { showPhotoSourceDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        if (hasPhoto) {
                            AsyncImage(
                                model = photoUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    TextButton(
                        onClick = { showPhotoSourceDialog = true },
                        modifier = Modifier.height(32.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(
                            text = if (hasPhoto) stringResource(id = com.callonly.launcher.R.string.photo_selected)
                                   else stringResource(id = com.callonly.launcher.R.string.add_photo),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text(stringResource(id = com.callonly.launcher.R.string.first_name)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = androidx.compose.ui.text.input.ImeAction.Next,
                        capitalization = androidx.compose.ui.text.input.KeyboardCapitalization.Sentences
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text(stringResource(id = com.callonly.launcher.R.string.last_name)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = androidx.compose.ui.text.input.ImeAction.Next,
                        capitalization = androidx.compose.ui.text.input.KeyboardCapitalization.Sentences
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it },
                    label = { Text(stringResource(id = com.callonly.launcher.R.string.number)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = androidx.compose.ui.text.input.ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (showPhotoSourceDialog) {
                    AlertDialog(
                        onDismissRequest = { showPhotoSourceDialog = false },
                        title = { Text(stringResource(id = com.callonly.launcher.R.string.photo_source), style = MaterialTheme.typography.titleMedium) },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Surface(
                                    onClick = {
                                        showPhotoSourceDialog = false
                                        photoPicker.launch(
                                            androidx.activity.result.PickVisualMediaRequest(
                                                androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
                                            )
                                        )
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(painter = androidx.compose.ui.res.painterResource(id = com.callonly.launcher.R.drawable.ic_photo_library), contentDescription = null)
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(stringResource(id = com.callonly.launcher.R.string.gallery))
                                    }
                                }
                                Surface(
                                    onClick = {
                                        showPhotoSourceDialog = false
                                        val permission = android.Manifest.permission.CAMERA
                                        if (androidx.core.content.ContextCompat.checkSelfPermission(
                                                context,
                                                permission
                                            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                                        ) {
                                            onOpenCamera { uri -> photoUri = uri }
                                        } else {
                                            cameraPermissionLauncher.launch(permission)
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(painter = androidx.compose.ui.res.painterResource(id = com.callonly.launcher.R.drawable.ic_photo_camera), contentDescription = null)
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(stringResource(id = com.callonly.launcher.R.string.camera))
                                    }
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
                        val fullName =
                            if (lastName.isNotBlank()) "$firstName $lastName" else firstName
                        onConfirm(fullName, number, photoUri?.toString())
                    }
                },
                enabled = firstName.isNotBlank() && number.isNotBlank()
            ) {
                Text(
                    if (contactToEdit == null) stringResource(id = com.callonly.launcher.R.string.add) else stringResource(
                        id = com.callonly.launcher.R.string.edit
                    )
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = com.callonly.launcher.R.string.cancel))
            }
        }
    )
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
            text = stringResource(id = com.callonly.launcher.R.string.settings_section_system),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
        )

        // Call Security
        val allowAllCalls by viewModel.allowAllCalls.collectAsState()
        ListItem(
            headlineContent = { Text(stringResource(id = com.callonly.launcher.R.string.accept_all_calls)) },
            supportingContent = { Text(stringResource(id = com.callonly.launcher.R.string.accept_all_calls_desc)) },
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
            headlineContent = { Text(stringResource(id = com.callonly.launcher.R.string.default_audio_output)) },
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
                           text = stringResource(id = com.callonly.launcher.R.string.earpiece),
                           style = MaterialTheme.typography.labelSmall,
                           textAlign = androidx.compose.ui.text.style.TextAlign.Center
                       )
                   }
                   SegmentedButton(
                       selected = isDefaultSpeakerEnabled,
                       onClick = { viewModel.setDefaultSpeakerEnabled(true) },
                       shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                   ) {
                       Text(
                           text = stringResource(id = com.callonly.launcher.R.string.speaker),
                           style = MaterialTheme.typography.labelSmall,
                           textAlign = androidx.compose.ui.text.style.TextAlign.Center
                       )
                   }
               }
            }
        )

        // Ringer Active
        val isRingerEnabled by viewModel.isRingerEnabled.collectAsState()
        ListItem(
            headlineContent = { Text(stringResource(id = com.callonly.launcher.R.string.ringer_active)) },
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
            headlineContent = { Text(stringResource(id = com.callonly.launcher.R.string.ringer_volume)) },
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
                        contentDescription = stringResource(id = com.callonly.launcher.R.string.test_ringer),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        // Screen & Display
        Text(
            text = stringResource(id = com.callonly.launcher.R.string.settings_section_display_power),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
        )

        // Plugged Behavior
        val screenBehaviorPlugged by viewModel.screenBehaviorPlugged.collectAsState()
        var showPluggedDialog by remember { mutableStateOf(false) }
        ListItem(
            headlineContent = { Text(stringResource(id = com.callonly.launcher.R.string.screen_behavior_plugged)) },
            leadingContent = {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(id = com.callonly.launcher.R.drawable.ic_battery_charging),
                    contentDescription = null
                )
            },
            supportingContent = {
                 Text(
                    when (screenBehaviorPlugged) {
                        0 -> stringResource(id = com.callonly.launcher.R.string.mode_off)
                        1 -> stringResource(id = com.callonly.launcher.R.string.mode_dim)
                        2 -> stringResource(id = com.callonly.launcher.R.string.mode_awake)
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
            headlineContent = { Text(stringResource(id = com.callonly.launcher.R.string.screen_behavior_battery)) },
            leadingContent = {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(id = com.callonly.launcher.R.drawable.ic_battery_full),
                    contentDescription = null
                )
            },
            supportingContent = {
                 Text(
                    when (screenBehaviorBattery) {
                        0 -> stringResource(id = com.callonly.launcher.R.string.mode_off)
                        1 -> stringResource(id = com.callonly.launcher.R.string.mode_dim)
                        2 -> stringResource(id = com.callonly.launcher.R.string.mode_awake)
                        else -> ""
                    }
                )
            },
            modifier = Modifier.clickable { showBatteryDialog = true }
        )

        // Night Mode
        ListItem(
            headlineContent = { Text(stringResource(id = com.callonly.launcher.R.string.night_mode)) },
            supportingContent = { Text(stringResource(id = com.callonly.launcher.R.string.night_mode_desc)) },
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
                    headlineContent = { Text(stringResource(id = com.callonly.launcher.R.string.night_start_label)) },
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
                    headlineContent = { Text(stringResource(id = com.callonly.launcher.R.string.night_end_label)) },
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
                    if (endTotalMinutes <= startTotalMinutes) stringResource(id = com.callonly.launcher.R.string.next_day) else ""
                    
                Text(
                     text = stringResource(
                        id = com.callonly.launcher.R.string.night_mode_duration_desc,
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
            if (viewModel.clockColor.collectAsState().value != 0) Color(viewModel.clockColor.collectAsState().value).toArgb() else HighContrastButtonBg.toArgb()

        ListItem(
            headlineContent = { Text(stringResource(id = com.callonly.launcher.R.string.clock_color)) },
            supportingContent = {
                 FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                ) {
                    colors.forEach { color ->
                        val isSelected = color.toArgb() == currentColor
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
            text = stringResource(id = com.callonly.launcher.R.string.settings_section_localization),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
        )

        var showLangDialog by remember { mutableStateOf(false) }
        ListItem(
            headlineContent = { Text(stringResource(id = com.callonly.launcher.R.string.language)) },
            supportingContent = {
                 val lang by viewModel.language.collectAsState()
                 Text(if (lang == "fr") "Français" else "English")
            },
            modifier = Modifier.clickable { showLangDialog = true }
        )

        val currentFormat by viewModel.timeFormat.collectAsState()
        ListItem(
            headlineContent = { Text(stringResource(id = com.callonly.launcher.R.string.time_format_title)) },
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
                            text = stringResource(id = com.callonly.launcher.R.string.time_format_12),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    SegmentedButton(
                        selected = currentFormat == "24",
                        onClick = { viewModel.setTimeFormat("24") },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) {
                        Text(
                            text = stringResource(id = com.callonly.launcher.R.string.time_format_24),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        )

        // Dialogs
        if (showPluggedDialog) {
            ScreenBehaviorDialog(
                title = stringResource(id = com.callonly.launcher.R.string.screen_behavior_plugged),
                iconRes = com.callonly.launcher.R.drawable.ic_battery_charging,
                currentValue = screenBehaviorPlugged,
                onConfirm = { viewModel.setScreenBehaviorPlugged(it); showPluggedDialog = false },
                onDismiss = { showPluggedDialog = false }
            )
        }

        if (showBatteryDialog) {
            ScreenBehaviorDialog(
                title = stringResource(id = com.callonly.launcher.R.string.screen_behavior_battery),
                iconRes = com.callonly.launcher.R.drawable.ic_battery_full,
                currentValue = screenBehaviorBattery,
                onConfirm = { viewModel.setScreenBehaviorBattery(it); showBatteryDialog = false },
                onDismiss = { showBatteryDialog = false }
            )
        }
        
        if (showLangDialog) {
             val lang by viewModel.language.collectAsState()
             val context = androidx.compose.ui.platform.LocalContext.current
             AlertDialog(
                 onDismissRequest = { showLangDialog = false },
                 title = { 
                     Text(
                         stringResource(id = com.callonly.launcher.R.string.language),
                         style = MaterialTheme.typography.headlineSmall
                     ) 
                 },
                 text = {
                     Column(
                         modifier = Modifier.fillMaxWidth(),
                         verticalArrangement = Arrangement.spacedBy(8.dp)
                     ) {
                         listOf(
                             Triple("fr", stringResource(id = com.callonly.launcher.R.string.language_french), "🇫🇷"),
                             Triple("en", stringResource(id = com.callonly.launcher.R.string.language_english), "🇬🇧")
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
                                     androidx.compose.material3.RadioButton(
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
                         Text(stringResource(id = com.callonly.launcher.R.string.cancel))
                     }
                 }
             )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
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

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onConfirm(
                    timePickerState.hour,
                    timePickerState.minute
                )
            }) {
                Text(androidx.compose.ui.res.stringResource(id = com.callonly.launcher.R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(androidx.compose.ui.res.stringResource(id = com.callonly.launcher.R.string.cancel))
            }
        },
        title = { Text(androidx.compose.ui.res.stringResource(id = com.callonly.launcher.R.string.select_hour)) },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.TimePicker(state = timePickerState)
            }
        }
    )
}

@Composable
fun ScreenBehaviorDialog(
    title: String,
    iconRes: Int? = null,
    currentValue: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (iconRes != null) {
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(id = iconRes),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    0 to stringResource(id = com.callonly.launcher.R.string.mode_off),
                    1 to stringResource(id = com.callonly.launcher.R.string.mode_dim),
                    2 to stringResource(id = com.callonly.launcher.R.string.mode_awake)
                ).forEach { (value, label) ->
                    val isSelected = currentValue == value
                    Surface(
                        onClick = { onConfirm(value) },
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
                                text = label,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            androidx.compose.material3.RadioButton(
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
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = com.callonly.launcher.R.string.cancel))
            }
        }
    )
}
