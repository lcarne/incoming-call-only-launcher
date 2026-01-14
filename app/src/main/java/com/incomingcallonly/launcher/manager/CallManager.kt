package com.incomingcallonly.launcher.manager

import android.content.Context
import android.telecom.Call
import android.telecom.CallAudioState
import android.telephony.PhoneNumberUtils
import com.incomingcallonly.launcher.R
import com.incomingcallonly.launcher.data.model.CallLog
import com.incomingcallonly.launcher.data.model.CallLogType
import com.incomingcallonly.launcher.data.repository.CallLogRepository
import com.incomingcallonly.launcher.data.repository.ContactRepository
import com.incomingcallonly.launcher.data.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext

@Singleton
class CallManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val callLogRepository: CallLogRepository,
    private val contactRepository: ContactRepository,
    private val settingsRepository: SettingsRepository
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

    private val _isSpeakerOn = MutableStateFlow(false)
    val isSpeakerOn: StateFlow<Boolean> = _isSpeakerOn.asStateFlow()

    interface AudioController {
        fun requestAudioRoute(route: Int)
    }

    private var audioController: AudioController? = null

    fun registerAudioController(controller: AudioController) {
        this.audioController = controller
    }

    fun unregisterAudioController() {
        this.audioController = null
    }

    private val callCallback = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            if (_isCallAllowed.value) {
                updateState(state)
            }
        }
    }

    private var currentAudioState: CallAudioState? = null

    fun onAudioStateChanged(audioState: CallAudioState?) {
        currentAudioState = audioState
        _isSpeakerOn.value = audioState?.route == CallAudioState.ROUTE_SPEAKER
    }

    fun setCall(call: Call) {
        currentCall = call
        wasAnswered = false
        userRejected = false
        answerTime = 0
        durationSeconds = 0
        _isCallAllowed.value = false
        _incomingNumber.value = null
        _callState.value = CallState.Idle

        // Register immediately to catch state changes during validation
        call.registerCallback(callCallback)

        val number = call.details.handle?.schemeSpecificPart

        scope.launch {
            val contacts = contactRepository.getContactsList()
            val isFavorite = number != null && contacts.any { contact ->
                @Suppress("DEPRECATION")
                PhoneNumberUtils.compare(contact.phoneNumber, number)
            }
            if (isFavorite || settingsRepository.allowAllCalls.value) {
                _incomingNumber.value = number
                _isCallAllowed.value = true
                updateState(call.details.state)
            } else {
                // Silently reject if not a contact
                call.reject(false, null)
                // Log it as BLOCKED if screening didn't already
                logCallInternal(number ?: context.getString(R.string.unknown_number), null, CallLogType.BLOCKED)
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
            Call.STATE_RINGING -> {
                _callState.value = CallState.Ringing
            }

            Call.STATE_ACTIVE -> {
                if (!wasAnswered) {
                    wasAnswered = true
                    answerTime = System.currentTimeMillis()
                    // Set speaker based on preference
                    setSpeakerOn(settingsRepository.isDefaultSpeakerEnabled.value)
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

            else -> {
                _callState.value = CallState.Idle
            }
        }
    }

    fun accept() {
        currentCall?.answer(0)
    }

    fun reject() {
        if (currentCall?.details?.state == Call.STATE_RINGING) {
            userRejected = true
            currentCall?.reject(false, null)
        } else {
            currentCall?.disconnect()
        }
    }

    fun clear() {
        currentCall?.unregisterCallback(callCallback)
        currentCall = null
        _incomingNumber.value = null
        _callState.value = CallState.Idle
        _isCallAllowed.value = false
        _isSpeakerOn.value = false
    }


    fun setSpeakerOn(on: Boolean) {
        val route = if (on) CallAudioState.ROUTE_SPEAKER else CallAudioState.ROUTE_WIRED_OR_EARPIECE
        audioController?.requestAudioRoute(route)
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
            val contact = contacts.find {
                @Suppress("DEPRECATION")
                PhoneNumberUtils.compare(it.phoneNumber, number)
            }

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
