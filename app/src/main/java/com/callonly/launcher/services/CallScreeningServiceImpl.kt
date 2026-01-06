package com.callonly.launcher.services

import android.telecom.Call
import android.telecom.CallScreeningService
import android.net.Uri
import android.telephony.PhoneNumberUtils
import com.callonly.launcher.data.model.CallLog
import com.callonly.launcher.data.model.CallLogType
import com.callonly.launcher.data.repository.CallLogRepository
import com.callonly.launcher.data.repository.ContactRepository
import com.callonly.launcher.data.repository.SettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CallScreeningServiceImpl : CallScreeningService() {

    @Inject
    lateinit var contactRepository: ContactRepository

    @Inject
    lateinit var callLogRepository: CallLogRepository

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onScreenCall(callDetails: Call.Details) {
        val handle = callDetails.handle
        if (handle == null) {
            blockCall(callDetails)
            return
        }

        val incomingNumber = handle.schemeSpecificPart

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Fetch all contacts to compare
                val contacts = contactRepository.getContactsList()
                
                // Check if any contact matches the incoming number
                val isFavorite = contacts.any { contact ->
                    @Suppress("DEPRECATION")
                    PhoneNumberUtils.compare(contact.phoneNumber, incomingNumber)
                }

                if (isFavorite || settingsRepository.allowAllCalls.value) {
                    allowCall(callDetails)
                } else {
                    logBlockedCall(incomingNumber)
                    blockCall(callDetails)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Fail safe: Allow or Block?
                // Given "Call Only" for favorites, maybe blocking is safer, but avoiding missed emergency calls?
                // Defaulting to block for strict requirement "Appels provenant de numéros non favoris sont automatiquement rejetés"
                blockCall(callDetails) 
            }
        }
    }

    private fun allowCall(callDetails: Call.Details) {
        val response = CallResponse.Builder()
            .setDisallowCall(false)
            .setRejectCall(false)
            .setSkipCallLog(false)
            .setSkipNotification(false)
            .build()
        respondToCall(callDetails, response)
    }

    private fun blockCall(callDetails: Call.Details) {
        val response = CallResponse.Builder()
            .setDisallowCall(true)
            .setRejectCall(true)
            .setSkipCallLog(true)
            .setSkipNotification(true)
            .build()
        respondToCall(callDetails, response)
    }

    private fun logBlockedCall(number: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val contacts = contactRepository.getContactsList()
            val contact = contacts.find { contact ->
                @Suppress("DEPRECATION")
                PhoneNumberUtils.compare(contact.phoneNumber, number)
            }
            
            callLogRepository.insertCallLog(
                CallLog(
                    number = number,
                    name = contact?.name,
                    type = CallLogType.BLOCKED
                )
            )
        }
    }
}
