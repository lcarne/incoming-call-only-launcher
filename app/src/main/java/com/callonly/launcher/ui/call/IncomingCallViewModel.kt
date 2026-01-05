package com.callonly.launcher.ui.call

import android.telephony.PhoneNumberUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callonly.launcher.data.model.Contact
import com.callonly.launcher.data.repository.ContactRepository
import com.callonly.launcher.manager.CallManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IncomingCallViewModel @Inject constructor(
    private val callManager: CallManager,
    private val contactRepository: ContactRepository
) : ViewModel() {

    val incomingCallState = combine(
        callManager.incomingNumber,
        callManager.callState,
        contactRepository.getAllContacts()
    ) { number, state, contacts -> 
        if (number == null) {
            IncomingCallUiState.Empty
        } else {
            // Find contact
            val contact = contacts.find { 
                 PhoneNumberUtils.compare(it.phoneNumber, number)
            }
            if (state == com.callonly.launcher.manager.CallState.Active) {
                IncomingCallUiState.Active(
                    number = number,
                    contact = contact
                )
            } else {
                IncomingCallUiState.Ringing(
                    number = number,
                    contact = contact
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = IncomingCallUiState.Empty
    )

    private val _callDuration = MutableStateFlow(0L)
    val callDuration: StateFlow<Long> = _callDuration.asStateFlow()

    init {
        viewModelScope.launch {
            incomingCallState.collect { state ->
                if (state is IncomingCallUiState.Active) {
                    startTimer()
                } else {
                    stopTimer()
                }
            }
        }
    }

    private var timerJob: kotlinx.coroutines.Job? = null
    private fun startTimer() {
        if (timerJob?.isActive == true) return
        val startTime = System.currentTimeMillis()
        timerJob = viewModelScope.launch {
            while (true) {
                _callDuration.value = (System.currentTimeMillis() - startTime) / 1000
                delay(1000)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        _callDuration.value = 0
    }

    fun acceptCall() {
        callManager.accept()
    }

    fun rejectCall() {
        callManager.reject()
    }

    fun endCall() {
        callManager.reject() // reject() in CallManager already calls disconnect()
    }
}

sealed class IncomingCallUiState {
    object Empty : IncomingCallUiState()
    data class Ringing(val number: String, val contact: Contact?) : IncomingCallUiState()
    data class Active(val number: String, val contact: Contact?) : IncomingCallUiState()
}
