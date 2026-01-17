package com.incomingcallonly.launcher.ui.admin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.incomingcallonly.launcher.data.model.Contact
import com.incomingcallonly.launcher.data.repository.ContactRepository
import com.incomingcallonly.launcher.util.ImageStorageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val repository: ContactRepository,
    private val imageStorageManager: ImageStorageManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val contacts = repository.getAllContacts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _importExportState = MutableStateFlow<Result<String>?>(null)
    val importExportState = _importExportState.asStateFlow()

    fun resetImportExportState() {
        _importExportState.value = null
    }

    fun addContact(name: String, number: String, photoUri: String?) {
        viewModelScope.launch {
            val localUri = photoUri?.let { uriStr ->
                imageStorageManager.saveImageLocally(android.net.Uri.parse(uriStr))
            }
            repository.insertContact(
                Contact(
                    name = name,
                    phoneNumber = number,
                    photoUri = localUri
                )
            )
        }
    }

    fun updateContact(contact: Contact) {
        viewModelScope.launch {
            val existingContact = repository.getContactById(contact.id)
            var newPhotoUri = contact.photoUri

            if (existingContact?.photoUri != contact.photoUri) {
                imageStorageManager.deleteImage(existingContact?.photoUri)
                newPhotoUri = contact.photoUri?.let { uriStr ->
                    if (!uriStr.startsWith("file://") || !uriStr.contains("contact_photos")) {
                        imageStorageManager.saveImageLocally(android.net.Uri.parse(uriStr))
                    } else {
                        uriStr
                    }
                }
            }
            repository.updateContact(contact.copy(photoUri = newPhotoUri))
        }
    }

    fun deleteContact(contact: Contact) {
        viewModelScope.launch {
            imageStorageManager.deleteImage(contact.photoUri)
            repository.deleteContact(contact)
        }
    }

    fun exportContacts(uri: android.net.Uri) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            _importExportState.value = Result.Loading
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    repository.exportContacts(outputStream)
                }
                _importExportState.value = Result.Success("Contacts exported successfully")
            } catch (e: Exception) {
                e.printStackTrace()
                _importExportState.value = Result.Error(e, "Failed to export contacts")
            }
        }
    }

    fun importContacts(uri: android.net.Uri) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            _importExportState.value = Result.Loading
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val json = inputStream.bufferedReader().use { it.readText() }
                    val count = repository.importContacts(json)
                    _importExportState.value = Result.Success("$count contacts imported successfully")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _importExportState.value = Result.Error(e, "Failed to import contacts")
            }
        }
    }
}
