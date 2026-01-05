package com.callonly.launcher.ui.call

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.callonly.launcher.data.model.Contact
import coil.compose.AsyncImage
// import com.callonly.launcher.R // Assuming resource for placeholder if needed, otherwise using vector

@Composable
fun IncomingCallScreen(
    viewModel: IncomingCallViewModel = hiltViewModel(),
    onCallRejected: () -> Unit,
    onCallEnded: () -> Unit
) {
    val uiState by viewModel.incomingCallState.collectAsState()
    val callDuration by viewModel.callDuration.collectAsState()

    var hasSeenCall by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    
    // Automatic finish when call ends
    LaunchedEffect(uiState) {
        if (uiState !is IncomingCallUiState.Empty) {
            hasSeenCall = true
        } else if (hasSeenCall) {
            onCallEnded()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Dark background for call screen
            .padding(16.dp)
    ) {
        when (val state = uiState) {
            is IncomingCallUiState.Empty -> {
                // Should probably not happen while activity is active and call is ringing
            }
            is IncomingCallUiState.Ringing -> {
                CallLayout(
                    number = state.number,
                    contact = state.contact,
                    state = state,
                    duration = callDuration,
                    viewModel = viewModel,
                    onCallRejected = onCallRejected
                )
            }
            is IncomingCallUiState.Active -> {
                CallLayout(
                    number = state.number,
                    contact = state.contact,
                    state = state,
                    duration = callDuration,
                    viewModel = viewModel,
                    onCallRejected = onCallRejected
                )
            }
        }
    }
}

private fun formatDuration(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) {
        String.format(java.util.Locale.getDefault(), "%02d:%02d:%02d", h, m, s)
    } else {
        String.format(java.util.Locale.getDefault(), "%02d:%02d", m, s)
    }
}

@Composable
private fun CallLayout(
    number: String,
    contact: Contact?,
    state: IncomingCallUiState,
    duration: Long,
    viewModel: IncomingCallViewModel,
    onCallRejected: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Contact Photo
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            ) {
                if (contact?.photoUri != null) {
                    AsyncImage(
                        model = contact.photoUri,
                        contentDescription = "Contact Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = rememberVectorPainter(Icons.Default.Person)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.Center),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Name
            Text(
                text = contact?.name ?: "Inconnu",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Number
            Text(
                text = number,
                style = MaterialTheme.typography.titleMedium,
                color = Color.LightGray
            )

            if (state is IncomingCallUiState.Active) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = formatDuration(duration),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.Green,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Appel en cours",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Green
                )
            }
        }

        // Buttons
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 64.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (state is IncomingCallUiState.Ringing) {
                // Decline Button (Small Red)
                Button(
                    onClick = {
                        viewModel.rejectCall()
                        onCallRejected()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = CircleShape,
                    modifier = Modifier.size(80.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Decline",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Answer Button (Large Green)
                Button(
                    onClick = {
                        viewModel.acceptCall()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                    shape = CircleShape,
                    modifier = Modifier.size(120.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Answer",
                        tint = Color.White,
                        modifier = Modifier.size(64.dp)
                    )
                }
            } else {
                // Active Call - Show End Call Button
                Button(
                    onClick = {
                        viewModel.endCall()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = CircleShape,
                    modifier = Modifier.size(100.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "End Call",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}
