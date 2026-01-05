package com.callonly.launcher.manager

import android.telecom.Call
import com.callonly.launcher.data.model.CallLog
import com.callonly.launcher.data.model.CallLogType
import com.callonly.launcher.data.repository.CallLogRepository
import com.callonly.launcher.data.repository.ContactRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import android.telephony.PhoneNumberUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallManager @Inject constructor(
    private val callLogRepository: CallLogRepository,
    private val contactRepository: ContactRepository
) {

    private var currentCall: Call? = null
    private var answerTime: Long = 0
    private var durationSeconds: Long = 0
    private var wasAnswered: Boolean = false
    private var userRejected: Boolean = false
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _callState = MutableStateFlow<CallState>(CallState.Idle)
    val callState: StateFlow<CallState> = _callState.asStateFlow()

    private val _incomingNumber = MutableStateFlow<String?>(null)
    val incomingNumber: StateFlow<String?> = _incomingNumber.asStateFlow()

    private val _isCallAllowed = MutableStateFlow(false)
    val isCallAllowed: StateFlow<Boolean> = _isCallAllowed.asStateFlow()

    fun setCall(call: Call) {
        currentCall = call
        wasAnswered = false
        userRejected = false
        answerTime = 0
        durationSeconds = 0
        _isCallAllowed.value = false
        _incomingNumber.value = null
        _callState.value = CallState.Idle
        
        val number = call.details.handle?.schemeSpecificPart
        
        scope.launch {
            val contacts = contactRepository.getContactsList()
            val isFavorite = contacts.any { contact ->
                PhoneNumberUtils.compare(contact.phoneNumber, number)
            }
            
            if (isFavorite) {
                _incomingNumber.value = number
                _isCallAllowed.value = true
                updateState(call.state)
                
                call.registerCallback(object : Call.Callback() {
                    override fun onStateChanged(call: Call, state: Int) {
                        updateState(state)
                    }
                })
            } else {
                // Silently reject if not a contact
                call.reject(false, null)
                // Log it as BLOCKED if screening didn't already
                if (number != null) {
                    logCallInternal(number, null, CallLogType.BLOCKED)
                }
                currentCall = null
            }
        }
    }

    private fun logCallInternal(number: String, contactName: String?, type: CallLogType) {
        scope.launch {
            callLogRepository.insertCallLog(
                CallLog(
                    number = number,
                    name = contactName,
                    durationSeconds = durationSeconds,
                    type = type
                )
            )
        }
    }

    fun updateState(state: Int) {
        when (state) {
            Call.STATE_RINGING -> _callState.value = CallState.Ringing
            Call.STATE_ACTIVE -> {
                if (!wasAnswered) {
                    wasAnswered = true
                    answerTime = System.currentTimeMillis()
                }
                _callState.value = CallState.Active
            }
            Call.STATE_DISCONNECTED -> {
                if (wasAnswered && answerTime > 0) {
                    durationSeconds = (System.currentTimeMillis() - answerTime) / 1000
                }
                _callState.value = CallState.Ended
                logCall()
                currentCall = null
            }
            else -> _callState.value = CallState.Idle
        }
    }

    fun accept() {
        currentCall?.answer(0)
    }

    fun reject() {
        if (currentCall?.state == Call.STATE_RINGING) {
            userRejected = true
            currentCall?.reject(false, null)
        } else {
            currentCall?.disconnect()
        }
    }

    fun clear() {
        currentCall = null
        _incomingNumber.value = null
        _callState.value = CallState.Idle
    }

    private fun logCall() {
        val number = _incomingNumber.value ?: return
        val type = when {
            wasAnswered -> CallLogType.INCOMING_ANSWERED
            userRejected -> CallLogType.INCOMING_REJECTED
            else -> CallLogType.INCOMING_MISSED
        }
        
        scope.launch {
            val contacts = contactRepository.getContactsList()
            val contact = contacts.find { it.phoneNumber == number }
            
            callLogRepository.insertCallLog(
                CallLog(
                    number = number,
                    name = contact?.name,
                    durationSeconds = durationSeconds,
                    type = type
                )
            )
        }
    }
}

sealed class CallState {
    object Idle : CallState()
    object Ringing : CallState()
    object Active : CallState()
    object Ended : CallState()
}
