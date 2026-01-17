package com.incomingcallonly.launcher.ui.admin.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.incomingcallonly.launcher.R
import com.incomingcallonly.launcher.ui.admin.AuthViewModel
import com.incomingcallonly.launcher.ui.admin.SettingsViewModel
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

@Composable
fun SettingsSystemSection(viewModel: SettingsViewModel, authViewModel: AuthViewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        AdminSectionHeader(text = stringResource(id = R.string.settings_section_system))

        AdminSettingsCard {
            // Call Security
            val allowAllCalls by viewModel.allowAllCalls.collectAsState()
            ListItem(
                colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
                headlineContent = { Text(stringResource(id = R.string.accept_all_calls)) },
                supportingContent = { Text(stringResource(id = R.string.accept_all_calls_desc)) },
                trailingContent = {
                    AdminSwitch(
                        checked = !allowAllCalls,
                        onCheckedChange = { viewModel.setAllowAllCalls(!it) }
                    )
                }
            )

            AdminDivider()

            val adminPin by authViewModel.adminPin.collectAsState()
            var showChangePinDialog by remember { mutableStateOf(false) }

            ListItem(
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                headlineContent = { Text(stringResource(id = R.string.change_pin)) },
                supportingContent = { Text("****") }, // Don't show actual PIN
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null
                    )
                },
                modifier = Modifier.clickable { showChangePinDialog = true }
            )

            if (showChangePinDialog) {
                var step by remember { mutableStateOf(1) }
                var previousPin by remember { mutableStateOf("") }
                var newPin by remember { mutableStateOf("") }
                var confirmPin by remember { mutableStateOf("") }
                var errorResId by remember { mutableStateOf<Int?>(null) }

                AdminDialog(
                    onDismissRequest = { showChangePinDialog = false },
                    title = stringResource(id = R.string.change_pin),
                    icon = Icons.Default.Lock,
                    animated = false,
                    content = {
                        Column {
                            when (step) {
                                1 -> {
                                    OutlinedTextField(
                                        value = previousPin,
                                        onValueChange = { 
                                            if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                                previousPin = it
                                                errorResId = null
                                            }
                                        },
                                        label = { Text(stringResource(id = R.string.previous_pin)) },
                                        singleLine = true,
                                        isError = errorResId != null,
                                        supportingText = { 
                                            Text(errorResId?.let { stringResource(it) } ?: "4 digits")
                                        },
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.NumberPassword
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                2 -> {
                                    OutlinedTextField(
                                        value = newPin,
                                        onValueChange = { 
                                            if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                                newPin = it 
                                            }
                                        },
                                        label = { Text(stringResource(id = R.string.new_pin)) },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.NumberPassword
                                        ),
                                        supportingText = { Text("4 digits") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                3 -> {
                                    OutlinedTextField(
                                        value = confirmPin,
                                        onValueChange = { 
                                            if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                                confirmPin = it
                                                errorResId = null
                                            }
                                        },
                                        label = { Text(stringResource(id = R.string.confirm_new_pin)) },
                                        singleLine = true,
                                        isError = errorResId != null,
                                        supportingText = { 
                                            Text(errorResId?.let { stringResource(it) } ?: "4 digits")
                                        },
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.NumberPassword
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                when (step) {
                                    1 -> {
                                        if (previousPin == adminPin) {
                                            step = 2
                                            errorResId = null
                                        } else {
                                            errorResId = R.string.incorrect_previous_pin
                                        }
                                    }
                                    2 -> {
                                        step = 3
                                    }
                                    3 -> {
                                        if (newPin == confirmPin) {
                                            authViewModel.changePin(newPin)
                                            showChangePinDialog = false
                                        } else {
                                            errorResId = R.string.pins_do_not_match
                                        }
                                    }
                                }
                            },
                            enabled = when (step) {
                                1 -> previousPin.length == 4
                                2 -> newPin.length == 4
                                3 -> confirmPin.length == 4
                                else -> false
                            }
                        ) {
                            Text(
                                if (step < 3) stringResource(id = R.string.next)
                                else stringResource(id = R.string.validate)
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { 
                            if (step > 1) {
                                step--
                                errorResId = null
                            } else {
                                showChangePinDialog = false
                            }
                        }) {
                            Text(
                                if (step > 1) stringResource(id = R.string.back)
                                else stringResource(id = R.string.cancel)
                            )
                        }
                    }
                )
            }
        }
    }
}
