package com.callonly.launcher.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.callonly.launcher.R

@Composable
fun PinEntryScreen(
    viewModel: AdminViewModel,
    onExit: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    val isError by viewModel.pinError.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header with Icon
            Icon(
                painter = painterResource(id = R.drawable.ic_lock_open),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                stringResource(id = R.string.admin_mode),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(48.dp))

            // PIN Display with modern dots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(4) { index ->
                    val isActive = index < pin.length
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(16.dp)
                            .background(
                                color = if (isActive) MaterialTheme.colorScheme.primary 
                                        else MaterialTheme.colorScheme.outlineVariant,
                                shape = CircleShape
                            )
                            .border(
                                width = if (isActive) 0.dp else 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = CircleShape
                            )
                    )
                }
            }

            Box(modifier = Modifier.height(40.dp)) {
                if (isError) {
                    Text(
                        stringResource(id = R.string.incorrect_pin),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Modern Material 3 Keypad
            val keys = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("", "0", "DEL")
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                keys.forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        row.forEach { key ->
                            when (key) {
                                "" -> {
                                    Spacer(modifier = Modifier.size(72.dp))
                                }
                                "DEL" -> {
                                    FilledTonalIconButton(
                                        onClick = {
                                            if (pin.isNotEmpty()) {
                                                pin = pin.dropLast(1)
                                            }
                                        },
                                        modifier = Modifier.size(72.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_arrow_back),
                                            contentDescription = "Delete"
                                        )
                                    }
                                }
                                else -> {
                                    OutlinedButton(
                                        onClick = {
                                            if (pin.length < 4) {
                                                pin += key
                                                if (pin.length == 4) {
                                                    viewModel.verifyPin(pin)
                                                }
                                            }
                                        },
                                        modifier = Modifier.size(72.dp),
                                        shape = CircleShape,
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text(
                                            text = key,
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            TextButton(
                onClick = onExit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(id = R.string.back),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }

    // Clear pin on error change if needed, or keep it to let user fix.
    // If error, maybe shake or clear? For now basic.
    LaunchedEffect(isError) {
        if (isError) {
            kotlinx.coroutines.delay(500)
            pin = "" // Auto clear on error after delay
        }
    }
}
