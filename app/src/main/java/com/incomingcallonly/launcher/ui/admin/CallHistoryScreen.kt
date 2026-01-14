package com.incomingcallonly.launcher.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ListItem
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.incomingcallonly.launcher.data.model.CallLog
import com.incomingcallonly.launcher.data.model.CallLogType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.activity.compose.BackHandler

private const val DATE_FORMAT_HISTORY = "dd/MM HH:mm"
private const val DURATION_FORMAT_HMS = "%02d:%02d:%02d"
private const val DURATION_FORMAT_MS = "%02d:%02d"
private const val SECONDS_PER_HOUR = 3600L
private const val SECONDS_PER_MINUTE = 60L

private val COLOR_ANSWERED = Color(0xFF4CAF50)
private val COLOR_MISSED = Color(0xFFF44336)
private val COLOR_REJECTED = Color(0xFFE91E63)
private val COLOR_BLOCKED = Color.Gray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallHistoryScreen(
    viewModel: AdminViewModel,
    onBack: () -> Unit
) {
    val callLogs by viewModel.callLogs.collectAsState()
    BackHandler(onBack = onBack)
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = com.incomingcallonly.launcher.R.string.call_history)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(id = com.incomingcallonly.launcher.R.drawable.ic_arrow_back),
                            contentDescription = stringResource(id = com.incomingcallonly.launcher.R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteConfirmation = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(id = com.incomingcallonly.launcher.R.string.clear_history_title),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (callLogs.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    stringResource(id = com.incomingcallonly.launcher.R.string.no_call_history),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                items(callLogs) { log ->
                    CallLogItem(log)
                }
            }
        }

        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text(stringResource(id = com.incomingcallonly.launcher.R.string.clear_history_title)) },
                text = { Text(stringResource(id = com.incomingcallonly.launcher.R.string.clear_history_confirm)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.clearCallHistory()
                            showDeleteConfirmation = false
                        }
                    ) {
                        Text(
                            stringResource(id = com.incomingcallonly.launcher.R.string.delete),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmation = false }) {
                        Text(stringResource(id = com.incomingcallonly.launcher.R.string.cancel))
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallLogItem(log: CallLog) {
    val sdf = SimpleDateFormat(DATE_FORMAT_HISTORY, Locale.getDefault())
    val dateStr = sdf.format(Date(log.timestamp))

    val (iconRes, color, label) = when (log.type) {
        CallLogType.INCOMING_ANSWERED -> Triple(
            com.incomingcallonly.launcher.R.drawable.ic_call,
            COLOR_ANSWERED,
            stringResource(id = com.incomingcallonly.launcher.R.string.call_received_label)
        )

        CallLogType.INCOMING_MISSED -> Triple(
            com.incomingcallonly.launcher.R.drawable.ic_call_missed,
            COLOR_MISSED,
            stringResource(id = com.incomingcallonly.launcher.R.string.call_missed_label)
        )

        CallLogType.INCOMING_REJECTED -> Triple(
            com.incomingcallonly.launcher.R.drawable.ic_block,
            COLOR_REJECTED,
            stringResource(id = com.incomingcallonly.launcher.R.string.call_rejected_label)
        )

        CallLogType.BLOCKED -> Triple(
            com.incomingcallonly.launcher.R.drawable.ic_block,
            COLOR_BLOCKED,
            stringResource(id = com.incomingcallonly.launcher.R.string.call_rejected_auto_label)
        )
    }

    ListItem(
        leadingContent = {
            Icon(
                painter = androidx.compose.ui.res.painterResource(id = iconRes),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        },
        headlineContent = {
            Text(
                text = log.name ?: log.number,
                style = MaterialTheme.typography.titleMedium
            )
        },
        supportingContent = {
            Column {
                if (log.name != null) {
                    Text(
                        text = log.number,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = color
                    )
                    if (log.durationSeconds > 0) {
                        Text(
                            text = " • ${formatDuration(log.durationSeconds)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        },
        trailingContent = {
            Text(
                text = dateStr,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    )
}

private fun formatDuration(seconds: Long): String {
    val h = seconds / SECONDS_PER_HOUR
    val m = (seconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE
    val s = seconds % SECONDS_PER_MINUTE
    return if (h > 0) {
        String.format(Locale.getDefault(), DURATION_FORMAT_HMS, h, m, s)
    } else {
        String.format(Locale.getDefault(), DURATION_FORMAT_MS, m, s)
    }
}
