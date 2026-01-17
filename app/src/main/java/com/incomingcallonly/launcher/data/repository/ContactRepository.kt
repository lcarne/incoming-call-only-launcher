package com.incomingcallonly.launcher.data.repository

import com.incomingcallonly.launcher.data.model.Contact
import kotlinx.coroutines.flow.Flow

interface ContactRepository {
    fun getAllContacts(): Flow<List<Contact>>
    suspend fun getContactsList(): List<Contact>
    suspend fun getContactById(id: Int): Contact?
    suspend fun insertContact(contact: Contact)
    suspend fun updateContact(contact: Contact)
    suspend fun deleteContact(contact: Contact)
    suspend fun deleteAllContacts()
    
    suspend fun exportContacts(outputStream: java.io.OutputStream)

    suspend fun importContacts(json: String): Int
}
