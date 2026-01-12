package com.callonly.launcher.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ClockDisplay(
    currentTime: Date,
    timeFormat: String,
    clockColor: Color,
    modifier: Modifier = Modifier
) {
    val is24Hour = timeFormat == "24"
    val timePattern = if (is24Hour) "HH:mm" else "hh:mm a"
    val timeFormatObj = SimpleDateFormat(timePattern, Locale.getDefault())
    val formattedTime = timeFormatObj.format(currentTime)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Text(
            text = formattedTime,
            style = androidx.compose.material3.MaterialTheme.typography.displayLarge.copy(
                fontSize = 120.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            ),
            color = clockColor
        )
    }
}
