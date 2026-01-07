package com.callonly.launcher.data.repository

import com.callonly.launcher.data.model.Contact
import kotlinx.coroutines.flow.Flow

interface ContactRepository {
    fun getAllContacts(): Flow<List<Contact>>
    suspend fun getContactsList(): List<Contact>
    suspend fun getContactById(id: Int): Contact?
    suspend fun insertContact(contact: Contact)
    suspend fun updateContact(contact: Contact)
    suspend fun deleteContact(contact: Contact)

    suspend fun exportContacts(): String

    suspend fun importContacts(json: String): Int
}
