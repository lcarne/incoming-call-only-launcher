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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.callonly.launcher.data.model.Contact
import androidx.compose.material3.Switch
import androidx.compose.material3.Slider
import java.text.SimpleDateFormat // For displaying hours sensibly if needed, or just Int
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
fun AdminContent(
    viewModel: AdminViewModel,
    onExit: () -> Unit,
    onUnpin: () -> Unit
) {
    var currentView by remember { mutableStateOf("SETTINGS") } // SETTINGS, CONTACTS, or HISTORY

    when (currentView) {
        "CONTACTS" -> {
            ContactManagementScreen(
                viewModel = viewModel,
                onBack = { currentView = "SETTINGS" }
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
        Text("Mode Administrateur", style = MaterialTheme.typography.headlineMedium)
        Text("Entrez le PIN (1234)", style = MaterialTheme.typography.bodyLarge)
        
        OutlinedTextField(
            value = pin,
            onValueChange = { pin = it },
            label = { Text("PIN") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = androidx.compose.ui.text.input.ImeAction.Done
            ),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                onDone = { viewModel.verifyPin(pin) }
            ),
            isError = isError,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        
        if (isError) {
            Text("Code incorrect", color = MaterialTheme.colorScheme.error)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.verifyPin(pin) }) {
                Text("Valider")
            }
            TextButton(onClick = onExit) {
                Text("Quitter")
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
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paramètres") },
                actions = {
                    TextButton(onClick = {
                         onUnpin()
                         viewModel.logout()
                         onExit()
                    }) {
                        Text("Déverrouiller")
                    }
                    TextButton(onClick = {
                        viewModel.logout()
                        onExit()
                    }) {
                        Text("Sortir")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Button(
                onClick = onManageContacts,
                modifier = Modifier.fillMaxWidth().height(64.dp),
                 colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                 Icon(com.callonly.launcher.ui.theme.StatusIcons.List, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                 Spacer(modifier = Modifier.width(16.dp))
                 Text("Gérer les contacts", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onShowHistory,
                modifier = Modifier.fillMaxWidth().height(64.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Icon(com.callonly.launcher.ui.theme.StatusIcons.Call, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                Spacer(modifier = Modifier.width(16.dp))
                Text("Historique des appels", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
            
            SettingsSection(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactManagementScreen(
    viewModel: AdminViewModel,
    onBack: () -> Unit
) {
    val contacts by viewModel.contacts.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var contactToEdit by remember { mutableStateOf<Contact?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contacts") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(com.callonly.launcher.ui.theme.StatusIcons.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Contact")
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
                    onDelete = { viewModel.deleteContact(contact) }
                )
            }
        }

        if (showAddDialog) {
            ContactDialog(
                contactToEdit = null,
                onDismiss = { showAddDialog = false },
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
            Icon(Icons.Default.Delete, contentDescription = "Delete")
        }
    }
}

@Composable
fun ContactDialog(
    contactToEdit: Contact?,
    onDismiss: () -> Unit,
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
                Text(if (contactToEdit == null) "Nouveau Contact" else "Modifier Contact")
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
                        Text("Importer du répertoire")
                    }
                }
            }
        },
        text = {
            Column {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("Prénom") },
                    modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Nom") },
                    modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()
                )
                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it },
                    label = { Text("Numéro") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()
                )
                Button(
                    onClick = { 
                        photoPicker.launch(
                            androidx.activity.result.PickVisualMediaRequest(
                                androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        ) 
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Text(if (photoUri != null) "Photo sélectionnée" else "Ajouter une photo")
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
                Text(if (contactToEdit == null) "Ajouter" else "Modifier")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
fun SettingsSection(viewModel: AdminViewModel) {
    val isAlwaysOn by viewModel.isAlwaysOnEnabled.collectAsState()
    val nightStart by viewModel.nightModeStartHour.collectAsState()
    val nightEnd by viewModel.nightModeEndHour.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Divider(modifier = Modifier.padding(vertical = 16.dp))
        Text("Paramètres d'écran", style = MaterialTheme.typography.titleLarge)
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Écran toujours allumé", style = MaterialTheme.typography.titleMedium)
                Text("Sauf la nuit (voir ci-dessous)", style = MaterialTheme.typography.bodySmall)
            }
            Switch(
                checked = isAlwaysOn,
                onCheckedChange = { viewModel.setAlwaysOnEnabled(it) }
            )
        }

        if (isAlwaysOn) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Mode Nuit (Écran éteint)", style = MaterialTheme.typography.titleMedium)
            
            Text("Début nuit: ${nightStart}h", style = MaterialTheme.typography.bodyMedium)
            Slider(
                value = nightStart.toFloat(),
                onValueChange = { viewModel.setNightModeStartHour(it.toInt()) },
                valueRange = 0f..23f,
                steps = 22
            )
            
            Text("Fin nuit: ${nightEnd}h", style = MaterialTheme.typography.bodyMedium)
            Slider(
                value = nightEnd.toFloat(),
                onValueChange = { viewModel.setNightModeEndHour(it.toInt()) },
                valueRange = 0f..23f,
                steps = 22
            )
        }
        
        Divider(modifier = Modifier.padding(vertical = 16.dp))
        Text("Couleur de l'horloge", style = MaterialTheme.typography.titleLarge)
        
        val colors = listOf(
            HighContrastButtonBg, // Default Blue/Yellow
            Color.White,
            Color.Cyan,
            Color.Green,
            Color.Magenta,
            Color.Red,
            Color.Yellow
        )
        val currentColor = if (viewModel.clockColor.collectAsState().value != -1) Color(viewModel.clockColor.collectAsState().value).toArgb() else HighContrastButtonBg.toArgb()

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
        ) {
            colors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color)
                        .clickable { viewModel.setClockColor(color.toArgb()) }
                        .border(
                            width = if (color.toArgb() == currentColor) 4.dp else 0.dp,
                            color = if (color.toArgb() == currentColor) Color.White else Color.Transparent,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}
