package com.callonly.launcher.ui.call
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import com.callonly.launcher.ui.theme.StatusIcons
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.callonly.launcher.data.model.Contact
import com.callonly.launcher.ui.components.BatteryLevelDisplay
import kotlinx.coroutines.delay

@Composable
fun IncomingCallScreen(
    viewModel: IncomingCallViewModel = hiltViewModel(),
    onCallRejected: () -> Unit,
    onCallEnded: () -> Unit
) {
    val uiState by viewModel.incomingCallState.collectAsState()
    val isSpeakerOn by viewModel.isSpeakerOn.collectAsState()

    var hasSeenCall by remember { mutableStateOf(false) }
    
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
            .background(Color.Black)
            .padding(16.dp)
    ) {
        when (val state = uiState) {
            is IncomingCallUiState.Empty -> {
                // Idle state
            }
            is IncomingCallUiState.Ringing -> {
                CallLayout(
                    number = state.number,
                    contact = state.contact,
                    state = state,
                    isSpeakerOn = false,
                    viewModel = viewModel,
                    onCallRejected = onCallRejected
                )
            }
            is IncomingCallUiState.Active -> {
                CallLayout(
                    number = state.number,
                    contact = state.contact,
                    state = state,
                    isSpeakerOn = isSpeakerOn,
                    viewModel = viewModel,
                    onCallRejected = onCallRejected
                )
            }
        }
    }
}

@Composable
fun CallLayout(
    number: String,
    contact: Contact?,
    state: IncomingCallUiState,
    isSpeakerOn: Boolean,
    viewModel: IncomingCallViewModel? = null,
    onCallRejected: (() -> Unit)? = null
) {
    val isRinging = state is IncomingCallUiState.Ringing
    var tapsRemaining by remember { mutableIntStateOf(2) }
    
    LaunchedEffect(tapsRemaining) {
        if (tapsRemaining < 2) {
            delay(3000)
            tapsRemaining = 2
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Battery Display at the very top
        BatteryLevelDisplay(
            modifier = Modifier,
            iconSize = 32.dp,
            fontSize = 20.sp
        )

        // --- ZONE HAUTE : Information (Non cliquable) ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = if (isRinging) 48.dp else 0.dp)
        ) {
            // Photo de l'appelant
            Box(
                modifier = Modifier
                    .size(if (isRinging) 300.dp else 220.dp)
                    .clip(if (isRinging) CircleShape else RoundedCornerShape(32.dp))
                    .background(Color.DarkGray)
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
                            .size(if (isRinging) 180.dp else 120.dp)
                            .align(Alignment.Center),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nom de l'appelant (Très gros caractères)
            Text(
                text = contact?.name ?: number,
                style = MaterialTheme.typography.displayMedium, // Extra large
                color = Color.White,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                lineHeight = androidx.compose.ui.unit.TextUnit.Unspecified
            )

            if (isRinging) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(id = com.callonly.launcher.R.string.incoming_call),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.LightGray,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        // --- ZONE CENTRALE : Action principale ---
        if (isRinging) {
            Spacer(modifier = Modifier.height(32.dp))
            // Ringing state: Refuse (left) and Answer (right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Left Column: Refuse
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (tapsRemaining < 2) {
                        Text(
                            text = stringResource(id = com.callonly.launcher.R.string.taps_remaining, tapsRemaining),
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(Color.Red, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    Button(
                        onClick = {
                            tapsRemaining--
                            if (tapsRemaining <= 0) {
                                viewModel?.rejectCall()
                                onCallRejected?.invoke()
                                tapsRemaining = 2
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red), // More vibrant red
                        shape = CircleShape,
                        modifier = Modifier.size(110.dp)
                    ) {
                        Icon(
                            imageVector = StatusIcons.CallEnd,
                            contentDescription = "Refuse",
                            tint = Color.White,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = stringResource(id = com.callonly.launcher.R.string.refuse),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Right Column: Answer
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = { viewModel?.acceptCall() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green), // More vibrant green
                        shape = CircleShape,
                        modifier = Modifier.size(160.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Answer",
                            tint = Color.White,
                            modifier = Modifier.size(90.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = stringResource(id = com.callonly.launcher.R.string.answer),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.Green,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        } else {
            // Active state: ONE HUGE RED HANGUP BUTTON
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (tapsRemaining < 2) {
                    Text(
                        text = stringResource(id = com.callonly.launcher.R.string.press_again, tapsRemaining),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(Color.Red.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    Spacer(Modifier.height(8.dp)) // Reduced from 16.dp
                }

                Button(
                    onClick = {
                        tapsRemaining--
                        if (tapsRemaining <= 0) {
                            viewModel?.endCall()
                            tapsRemaining = 2
                        }
                    },
                    modifier = Modifier.size(140.dp), 
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = CircleShape,
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Icon(
                        imageVector = StatusIcons.CallEnd,
                        contentDescription = "End Call",
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )
                }
                Spacer(Modifier.height(8.dp)) // Reduced from 16.dp
                Text(
                    text = stringResource(id = com.callonly.launcher.R.string.hang_up),
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.Red,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }


        // --- ZONE BASSE : Options audio (Active state only) ---
        if (!isRinging) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Earpiece Button (Oreille - Left)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = { viewModel?.setSpeakerOn(false) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isSpeakerOn) Color(0xFF2196F3) else Color.DarkGray
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.size(110.dp)
                    ) {
                        Icon(
                            imageVector = StatusIcons.Hearing,
                            contentDescription = "Earpiece",
                            tint = if (!isSpeakerOn) Color.White else Color.Gray,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = com.callonly.launcher.R.string.earpiece),
                        style = MaterialTheme.typography.titleLarge,
                        color = if (!isSpeakerOn) Color(0xFF2196F3) else Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                // Speaker Button (Parleur - Right)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = { viewModel?.setSpeakerOn(true) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSpeakerOn) Color.Green else Color.DarkGray
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.size(110.dp)
                    ) {
                        Icon(
                            imageVector = StatusIcons.Speaker,
                            contentDescription = "Speaker",
                            tint = if (isSpeakerOn) Color.Black else Color.White,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = com.callonly.launcher.R.string.speaker),
                        style = MaterialTheme.typography.titleLarge,
                        color = if (isSpeakerOn) Color.Green else Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Empty space for ringing mode to keep layout stable if needed, but Arrange.SpaceBetween handles it
            Spacer(Modifier.height(48.dp))
        }
    }
}
