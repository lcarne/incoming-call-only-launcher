package com.callonly.launcher.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.callonly.launcher.R
import com.callonly.launcher.ui.theme.Black
import com.callonly.launcher.ui.theme.ErrorRed
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
    FilledTonalButton(
        onClick = {
            if (!isNight) {
                onToggleRinger()
            }
        },
        modifier = Modifier
            .width(300.dp)
            .height(160.dp),
        enabled = !isNight,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = if (isNight) ErrorRed.copy(alpha = 0.5f) 
                             else if (isRingerEnabled) accentColor 
                             else ErrorRed,
            contentColor = if (isNight) White.copy(alpha = 0.5f) 
                           else if (isRingerEnabled) Black 
                           else White,
            disabledContainerColor = ErrorRed.copy(alpha = 0.3f),
            disabledContentColor = White.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(32.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isNight) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_volume_off),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        id = R.string.night_mode_active_ringer_off,
                        String.format("%02dh%02d", nightStart, nightStartMin),
                        String.format("%02dh%02d", nightEnd, nightEndMin)
                    ),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            } else {
                Icon(
                    painter = painterResource(
                        id = if (isRingerEnabled) R.drawable.ic_volume_up else R.drawable.ic_volume_off
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isRingerEnabled) stringResource(id = R.string.ringer_active) 
                           else stringResource(id = R.string.ringer_disabled),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
