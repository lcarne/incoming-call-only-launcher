package com.callonly.launcher.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.callonly.launcher.data.model.CallLog
import com.callonly.launcher.data.model.CallLogType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallHistoryScreen(
    viewModel: AdminViewModel,
    onBack: () -> Unit
) {
    val callLogs by viewModel.callLogs.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historique des appels") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(com.callonly.launcher.ui.theme.StatusIcons.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.clearCallHistory() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear all", tint = MaterialTheme.colorScheme.error)
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
                Text("Aucun appel dans l'historique", style = MaterialTheme.typography.bodyLarge)
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
    }
}

@Composable
fun CallLogItem(log: CallLog) {
    val sdf = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
    val dateStr = sdf.format(Date(log.timestamp))

    val (icon, color, label) = when (log.type) {
        CallLogType.INCOMING_ANSWERED -> Triple(com.callonly.launcher.ui.theme.StatusIcons.Call, Color(0xFF4CAF50), "Reçu")
        CallLogType.INCOMING_MISSED -> Triple(com.callonly.launcher.ui.theme.StatusIcons.CallMissed, Color(0xFFF44336), "Manqué")
        CallLogType.INCOMING_REJECTED -> Triple(com.callonly.launcher.ui.theme.StatusIcons.Block, Color(0xFFE91E63), "Refusé")
        CallLogType.BLOCKED -> Triple(com.callonly.launcher.ui.theme.StatusIcons.Block, Color.Gray, "Refusé (auto)")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(32.dp)
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = log.name ?: log.number,
                style = MaterialTheme.typography.titleMedium
            )
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
        
        Text(
            text = dateStr,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

private fun formatDuration(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) {
        String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)
    } else {
        String.format(Locale.getDefault(), "%02d:%02d", m, s)
    }
}
