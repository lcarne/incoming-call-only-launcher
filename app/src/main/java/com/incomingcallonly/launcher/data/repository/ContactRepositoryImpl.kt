package com.incomingcallonly.launcher.data.repository

import android.content.Context
import androidx.core.net.toUri
import com.incomingcallonly.launcher.data.local.ContactDao
import com.incomingcallonly.launcher.data.model.Contact
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val contactDao: ContactDao,
    @ApplicationContext private val context: Context
) : ContactRepository {
    override fun getAllContacts(): Flow<List<Contact>> = contactDao.getAllContacts()

    override suspend fun getContactsList(): List<Contact> = contactDao.getContactsList()

    override suspend fun getContactById(id: Int): Contact? = contactDao.getContactById(id)

    override suspend fun insertContact(contact: Contact) = contactDao.insertContact(contact)

    override suspend fun updateContact(contact: Contact) = contactDao.updateContact(contact)

    override suspend fun deleteContact(contact: Contact) = contactDao.deleteContact(contact)

    override suspend fun deleteAllContacts() = contactDao.deleteAllContacts()

    override suspend fun exportContacts(outputStream: java.io.OutputStream) {
        val contacts = contactDao.getContactsList()
        outputStream.bufferedWriter().use { writer ->
            val jsonWriter = com.google.gson.stream.JsonWriter(writer)
            jsonWriter.beginArray()
            
            contacts.forEach { contact ->
                jsonWriter.beginObject()
                
                val names = contact.name.split(" ")
                val firstName = if (names.isNotEmpty()) names.first() else ""
                val lastName = if (names.size > 1) names.drop(1).joinToString(" ") else ""
                
                jsonWriter.name("firstName").value(firstName)
                jsonWriter.name("lastName").value(lastName)
                jsonWriter.name("phoneNumber").value(contact.phoneNumber)
                
                val photoBase64 = contact.photoUri?.let { uriString ->
                    try {
                        val uri = uriString.toUri()
                        context.contentResolver.openInputStream(uri)?.use { inputStream ->
                            val bytes = inputStream.readBytes()
                            android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)
                        }
                    } catch (e: Exception) {
                        null
                    }
                }
                
                if (photoBase64 != null) {
                    jsonWriter.name("photoBase64").value(photoBase64)
                }
                
                jsonWriter.endObject()
            }
            
            jsonWriter.endArray()
        }
    }

    override suspend fun importContacts(json: String): Int {
        val type = object :
            com.google.gson.reflect.TypeToken<List<com.incomingcallonly.launcher.data.model.ContactExportDto>>() {}.type
        val importList: List<com.incomingcallonly.launcher.data.model.ContactExportDto> =
            com.google.gson.Gson().fromJson(json, type)

        var count = 0
        importList.filter { !it.phoneNumber.isNullOrBlank() }.forEach { dto ->
            // Check if contact already exists (simple check by number)
            val number = dto.phoneNumber!!
            val existing = contactDao.getContactsList().find { it.phoneNumber == number }
            if (existing == null) {
                var photoUri: String? = null
                dto.photoBase64?.let { base64 ->
                    try {
                        val bytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
                        val fileName = "imported_contact_${System.currentTimeMillis()}_${count}.jpg"
                        val file = java.io.File(context.filesDir, fileName)
                        java.io.FileOutputStream(file).use { it.write(bytes) }
                        photoUri = android.net.Uri.fromFile(file).toString()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                val first = dto.firstName ?: ""
                val last = dto.lastName ?: ""
                val fullName = if (last.isNotBlank()) "$first $last" else first
                
                val finalName = if (fullName.isBlank()) "Unknown" else fullName

                val newContact = Contact(
                    name = finalName,
                    phoneNumber = number,
                    photoUri = photoUri
                )
                contactDao.insertContact(newContact)
                count++
            }
        }
        return count
    }
}
