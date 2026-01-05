package com.callonly.launcher.services

import android.telecom.Call
import android.telecom.CallScreeningService
import android.net.Uri
import android.telephony.PhoneNumberUtils
import com.callonly.launcher.data.model.CallLog
import com.callonly.launcher.data.model.CallLogType
import com.callonly.launcher.data.repository.CallLogRepository
import com.callonly.launcher.data.repository.ContactRepository
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
                    // Simplest check: exact containment or robust comparison
                    // contact.phoneNumber might be "06 12 34...", incoming might be "+3361234..."
                    PhoneNumberUtils.compare(contact.phoneNumber, incomingNumber)
                }

                if (isFavorite) {
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
            callLogRepository.insertCallLog(
                CallLog(
                    number = number,
                    type = CallLogType.BLOCKED
                )
            )
        }
    }
}
