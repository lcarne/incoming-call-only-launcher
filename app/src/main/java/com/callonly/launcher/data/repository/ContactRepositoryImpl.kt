package com.callonly.launcher.data.repository

import com.callonly.launcher.data.local.ContactDao
import com.callonly.launcher.data.model.Contact
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val contactDao: ContactDao
) : ContactRepository {
    override fun getAllContacts(): Flow<List<Contact>> = contactDao.getAllContacts()

    override suspend fun getContactsList(): List<Contact> = contactDao.getContactsList()

    override suspend fun getContactById(id: Int): Contact? = contactDao.getContactById(id)

    override suspend fun insertContact(contact: Contact) = contactDao.insertContact(contact)

    override suspend fun updateContact(contact: Contact) = contactDao.updateContact(contact)

    override suspend fun deleteContact(contact: Contact) = contactDao.deleteContact(contact)
}
