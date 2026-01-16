package com.incomingcallonly.launcher.ui.onboarding

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.incomingcallonly.launcher.R
import com.incomingcallonly.launcher.ui.theme.ConfirmGreen
import com.incomingcallonly.launcher.ui.theme.ErrorRed
import kotlinx.coroutines.delay

@Composable
fun OnboardingFlow(onDismiss: () -> Unit) {
    var step by remember { mutableStateOf(0) }
    val context = androidx.compose.ui.platform.LocalContext.current

    // 0: Presentation
    // 1: Authorization Requests Explanation
    // 2: Request Contacts
    // 3: Request Phone State
    // 4: Location Explanation
    // 5: Request Location
    // 6: Default Dialer
    // 7: Default Launcher
    // 8: Admin Explanation

    val contactLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { _ -> step++ }
    )
    
    val phoneLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { _ -> step++ }
    )

    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { _ -> step++ }
    )

    val dialerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { _ -> step++ }
    )

    AlertDialog(
        onDismissRequest = { /* Prevent dismissal */ },
        icon = {
            androidx.compose.material3.Icon(
                imageVector = when (step) {
                    0 -> Icons.Default.Info
                    1, 8 -> Icons.Default.Lock
                    2 -> Icons.Default.Person
                    3, 6 -> Icons.Default.Phone
                    4, 5 -> Icons.Default.LocationOn
                    7 -> Icons.Default.Home // Need Home icon import
                    else -> Icons.Default.Info
                },
                contentDescription = null
            )
        },
        title = {
            Text(
                text = stringResource(id = when (step) {
                    0 -> R.string.onboarding_presentation_title
                    1 -> R.string.onboarding_auth_intro_title
                    2 -> R.string.onboarding_auth_contacts_title
                    3 -> R.string.onboarding_auth_calls_title
                    4 -> R.string.onboarding_auth_location_intro_title
                    5 -> R.string.onboarding_auth_location_request_title
                    6 -> R.string.onboarding_default_dialer_title
                    7 -> R.string.onboarding_default_launcher_title
                    else -> R.string.onboarding_admin_intro_title
                }),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column {
                Text(
                    text = parseBoldString(stringResource(id = when (step) {
                        0 -> R.string.onboarding_presentation_message
                        1 -> R.string.onboarding_auth_intro_message
                        2 -> R.string.onboarding_auth_contacts_message
                        3 -> R.string.onboarding_auth_calls_message
                        4 -> R.string.onboarding_auth_location_intro_message
                        5 -> R.string.onboarding_auth_location_request_message
                        6 -> R.string.onboarding_default_dialer_message
                        7 -> R.string.onboarding_default_launcher_message
                        else -> R.string.onboarding_admin_intro_message
                    })),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (step == 8) {
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
        confirmButton = {
            if (step == 8) {
                // Admin Step Logic (Double Tap)
                var tapCount by remember { mutableStateOf(0) }

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
                            0, 1, 4 -> step++
                            2 -> contactLauncher.launch(android.Manifest.permission.READ_CONTACTS)
                            3 -> {
                                val permissions = mutableListOf(
                                    android.Manifest.permission.READ_PHONE_STATE,
                                    android.Manifest.permission.ANSWER_PHONE_CALLS
                                )
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                    permissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
                                }
                                phoneLauncher.launch(permissions.toTypedArray())
                            }
                            5 -> locationLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                            6 -> {
                                // Request Default Dialer
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                                    val roleManager = context.getSystemService(android.app.role.RoleManager::class.java)
                                    if (roleManager?.isRoleAvailable(android.app.role.RoleManager.ROLE_DIALER) == true &&
                                        !roleManager.isRoleHeld(android.app.role.RoleManager.ROLE_DIALER)) {
                                        val intent = roleManager.createRequestRoleIntent(android.app.role.RoleManager.ROLE_DIALER)
                                        dialerLauncher.launch(intent)
                                    } else {
                                        step++
                                    }
                                } else {
                                    val telecomManager = context.getSystemService(android.telecom.TelecomManager::class.java)
                                    if (telecomManager?.defaultDialerPackage != context.packageName) {
                                        @Suppress("DEPRECATION")
                                        val intent = android.content.Intent(android.telecom.TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).apply {
                                            putExtra(android.telecom.TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, context.packageName)
                                        }
                                        dialerLauncher.launch(intent)
                                    } else {
                                        step++
                                    }
                                }
                            }
                            7 -> {
                                // Request Default Launcher
                                // For launcher, we can't easily check if we are default *before* launching intent inside a generic function without more context/logic,
                                // but we can just launch the settings.
                                // Unlike dialer, there is no result callback that guarantees we are set.
                                // We'll just launch it and move next.
                                val intent = android.content.Intent(android.provider.Settings.ACTION_HOME_SETTINGS)
                                context.startActivity(intent)
                                step++
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                ) {
                    Text(
                        text = stringResource(id = if (step in listOf(2, 3, 5, 6, 7)) R.string.validate else R.string.next),
                        fontSize = 18.sp
                    )
                }
            }
        },
        modifier = Modifier.padding(16.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun parseBoldString(text: String): androidx.compose.ui.text.AnnotatedString {
    return buildAnnotatedString {
        val parts = text.split("*")
        parts.forEachIndexed { index, part ->
            if (index % 2 == 1) { // Odd indices are inside * *
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(part)
                }
            } else {
                append(part)
            }
        }
    }
}
