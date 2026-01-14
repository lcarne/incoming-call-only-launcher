package com.incomingcallonly.launcher.ui.admin.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.incomingcallonly.launcher.R
import com.incomingcallonly.launcher.data.model.Contact

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
    LaunchedEffect(contactToEdit) {
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

    val context = LocalContext.current

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

    var showDiscardConfirmation by remember { mutableStateOf(false) }
    
    // Check if form is modified
    fun isModified(): Boolean {
        if (contactToEdit == null) {
            return firstName.isNotBlank() || lastName.isNotBlank() || number.isNotBlank() || photoUri != null
        }
        val names = contactToEdit.name.split(" ")
        val originalFirst = names.firstOrNull() ?: ""
        val originalLast = if (names.size > 1) names.drop(1).joinToString(" ") else ""
        val originalPhone = contactToEdit.phoneNumber
        val originalPhoto = contactToEdit.photoUri
        val currentPhoto = photoUri?.toString()
        
        return firstName != originalFirst || lastName != originalLast || number != originalPhone || currentPhoto != originalPhoto
    }
    
    val attemptDismiss = {
        if (isModified()) {
            showDiscardConfirmation = true
        } else {
            onDismiss()
        }
    }

    if (showDiscardConfirmation) {
        AlertDialog(
            onDismissRequest = { showDiscardConfirmation = false },
            title = { Text(stringResource(R.string.discard_changes_title)) },
            text = { Text(stringResource(R.string.discard_changes_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardConfirmation = false
                        onDismiss()
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardConfirmation = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    AlertDialog(
        onDismissRequest = attemptDismiss,
        title = {
            Text(
                text = if (contactToEdit == null) stringResource(id = R.string.new_contact) else stringResource(
                    id = R.string.edit_contact
                ),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val focusManager = LocalFocusManager.current

                // Compact Import Button
                if (contactToEdit == null) {
                    SuggestionChip(
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
                                stringResource(id = R.string.import_from_directory),
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
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(
                            text = if (hasPhoto) stringResource(id = R.string.photo_selected)
                                   else stringResource(id = R.string.add_photo),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text(stringResource(id = R.string.first_name)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text(stringResource(id = R.string.last_name)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it },
                    label = { Text(stringResource(id = R.string.number)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (showPhotoSourceDialog) {
                    AlertDialog(
                        onDismissRequest = { showPhotoSourceDialog = false },
                        title = { Text(stringResource(id = R.string.photo_source), style = MaterialTheme.typography.titleMedium) },
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
                                        Icon(painter = painterResource(id = R.drawable.ic_photo_library), contentDescription = null)
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(stringResource(id = R.string.gallery))
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
                                        Icon(painter = painterResource(id = R.drawable.ic_photo_camera), contentDescription = null)
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(stringResource(id = R.string.camera))
                                    }
                                }
                            }
                        },
                        confirmButton = {},
                        dismissButton = {
                            TextButton(onClick = { showPhotoSourceDialog = false }) {
                                Text(stringResource(id = R.string.cancel))
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
                    if (contactToEdit == null) stringResource(id = R.string.add) else stringResource(
                        id = R.string.edit
                    )
                )
            }
        },
        dismissButton = {
            TextButton(onClick = attemptDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}
