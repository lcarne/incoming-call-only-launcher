package com.callonly.launcher.ui.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.callonly.launcher.R
import com.callonly.launcher.ui.theme.DarkGray
import com.callonly.launcher.ui.theme.White

@Composable
fun RingerControl(
    isNight: Boolean,
    isRingerEnabled: Boolean,
    nightStart: Int,
    nightStartMin: Int,
    nightEnd: Int,
    nightEndMin: Int,
    accentColor: Color,
    onToggleRinger: () -> Unit
) {
    if (isNight) {
        // Night Mode Info - Read Only
        Surface(
            color = DarkGray,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_volume_off),
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        R.string.night_mode_active_ringer_off,
                        String.format("%02d:%02d", nightStart, nightStartMin),
                        String.format("%02d:%02d", nightEnd, nightEndMin)
                    ),
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    } else {
        // Day Mode - Interactive Ringer Toggle
        Surface(
            color = if (isRingerEnabled) White else DarkGray,
            shape = CircleShape,
            modifier = Modifier.clickable { onToggleRinger() }
        ) {
            Box(
                modifier = Modifier
                    .padding(24.dp)
                    .size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isRingerEnabled) R.drawable.ic_volume_up else R.drawable.ic_volume_off
                    ),
                    contentDescription = "Toggle Ringer",
                    tint = if (isRingerEnabled) accentColor else White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}
