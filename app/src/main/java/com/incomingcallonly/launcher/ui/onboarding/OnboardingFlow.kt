package com.incomingcallonly.launcher.ui.onboarding

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.incomingcallonly.launcher.R
import com.incomingcallonly.launcher.ui.components.AppDialog
import com.incomingcallonly.launcher.ui.components.parseBoldString
import com.incomingcallonly.launcher.ui.theme.ConfirmGreen
import com.incomingcallonly.launcher.ui.theme.ErrorRed
import kotlinx.coroutines.delay

@Composable
fun OnboardingFlow(onDismiss: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }

    // Stepping Logic:
    // 0: Presentation
    // 4: Location Explanation
    // 6: Admin Explanation

    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { step = 6 }
    )

    val currentIcon = when (step) {
        0 -> Icons.Default.Info
        4 -> Icons.Default.LocationOn
        6 -> Icons.Default.Lock
        else -> Icons.Default.Info
    }

    val currentTitle = stringResource(id = when (step) {
        0 -> R.string.onboarding_presentation_title
        4 -> R.string.onboarding_auth_location_intro_title
        else -> R.string.onboarding_admin_intro_title
    })

    val currentMessage = stringResource(id = when (step) {
        0 -> R.string.onboarding_presentation_message
        4 -> R.string.onboarding_auth_location_intro_message
        else -> R.string.onboarding_admin_intro_message
    })

    AppDialog(
        onDismissRequest = { /* Prevent dismissal */ },
        icon = currentIcon,
        title = currentTitle,
        // For step 6, we use custom content to show the error message.
        // For other steps, we just show the message using the simplified content slot
        // or just passing message if separate. 
        // Since we want consistent aligned text, we use content slot with parseBoldString.
        content = {
            Column {
                Text(
                    text = parseBoldString(currentMessage),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start
                )
                
                if (step == 6) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(id = R.string.onboarding_important),
                        style = MaterialTheme.typography.titleMedium,
                        color = ErrorRed,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        buttons = {
            if (step == 6) {
                // Admin Step Logic (Double Tap)
                var tapCount by remember { mutableIntStateOf(0) }

                LaunchedEffect(tapCount) {
                    if (tapCount > 0) {
                        delay(2000)
                        if (tapCount == 1) {
                            tapCount = 0
                        }
                    }
                }

                Button(
                    onClick = {
                        if (tapCount == 0) {
                            tapCount++
                        } else {
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (tapCount == 0) MaterialTheme.colorScheme.primary else ConfirmGreen
                    )
                ) {
                    Text(
                        text = if (tapCount == 0) stringResource(id = R.string.understood) else stringResource(
                            id = R.string.press_again_to_confirm
                        ),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                }
            } else {
                Button(
                    onClick = {
                        when (step) {
                            0 -> step = 4
                            4 -> locationLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                ) {
                    // Display logic:
                    // 0 -> 1/3
                    // 4 -> 2/3
                    val currentStepDisplay = when(step) {
                        0 -> 1
                        4 -> 2
                        else -> 0
                    }
                    val buttonText = stringResource(id = if (step == 4) R.string.validate else R.string.next)
                    Text(
                        text = if (currentStepDisplay > 0) "$buttonText ($currentStepDisplay/3)" else buttonText,
                        fontSize = 18.sp
                    )
                }
            }
        }
    )
}
