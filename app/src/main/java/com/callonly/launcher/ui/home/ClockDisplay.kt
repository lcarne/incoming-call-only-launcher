package com.callonly.launcher.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ClockDisplay(
    currentTime: Date,
    timeFormat: String,
    clockColor: Color
) {
    // Format Pattern Logic
    val timeFormatPattern = remember(timeFormat) {
        if (timeFormat == "12") "hh:mm a" else "HH:mm"
    }
    
    // We use SimpleDateFormat directly here based on the pattern
    val formatter = remember(timeFormatPattern) {
        SimpleDateFormat(timeFormatPattern, Locale.getDefault())
    }

    if (timeFormat == "12") {
        val timeOnly = SimpleDateFormat("hh:mm", Locale.getDefault()).format(currentTime)
        val ampm = SimpleDateFormat("a", Locale.getDefault()).format(currentTime)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = timeOnly,
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 120.sp),
                color = clockColor,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = ampm,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 28.sp),
                color = clockColor,
                textAlign = TextAlign.Center
            )
        }
    } else {
        Text(
            text = formatter.format(currentTime),
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 120.sp),
            color = clockColor,
            textAlign = TextAlign.Center
        )
    }
}
