package com.callonly.launcher.ui.components

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.telephony.SignalStrength
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.callonly.launcher.R
import com.callonly.launcher.ui.theme.ErrorRed
import com.callonly.launcher.ui.theme.White

@Composable
fun BatteryLevelDisplay(
    modifier: Modifier = Modifier,
    iconSize: androidx.compose.ui.unit.Dp = 48.dp,
    fontSize: androidx.compose.ui.unit.TextUnit = 32.sp
) {
    val context = LocalContext.current
    var batteryLevel by remember { mutableStateOf<Pair<Int, Boolean>?>(null) }

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL

                if (level >= 0 && scale > 0) {
                    val pct = (level * 100) / scale
                    batteryLevel = Pair(pct, isCharging)
                }
            }
        }
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(receiver, filter)
        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    if (batteryLevel != null) {
        val (level, isCharging) = batteryLevel!!
        
        // Determine icon based on level
        val iconRes = when {
            isCharging -> R.drawable.ic_battery_charging
            level <= 20 -> R.drawable.ic_battery_alert
            level <= 50 -> R.drawable.ic_battery_50
            level <= 80 -> R.drawable.ic_battery_80
            else -> R.drawable.ic_battery_full
        }

        // Determine color based on level (keep existing color logic where possible or adapt)
        // Original logic: <=20 Red, <=50 Yellow, Else Green. 
        // We can stick to that or use the icon's intrinsic color (Unspecified).
        // Since the XMLs have colors hardcoded, we should use Color.Unspecified to show them!
        // EXCEPT for charging which is Black in XML, maybe we want it White or colored?
        // The original code used Tint.
        // The user's prompt "je te laisse libre dans ... les couleurs" and "Resource XML" suggests using the XML colors.
        // My XMLs have colors naturally (Green, Orange, etc). 
        // ic_battery_charging I made black. Let's make it white or follow theme?
        // In the existing UI, tint was used.
        // Let's use Color.Unspecified for the battery levels to show the XML colors.
        // For charging, let's allow tinting or use a specific color.
        
        val tint = if (isCharging) White else Color.Unspecified

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            Icon(
                painter = androidx.compose.ui.res.painterResource(id = iconRes),
                contentDescription = stringResource(id = R.string.battery_level_desc),
                tint = tint,
                modifier = Modifier
                    .size(iconSize)
                    .padding(end = 8.dp)
            )
            Text(
                text = "$level%",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = fontSize,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                ),
                color = Color.LightGray
            )
        }
    }
}

@Composable
fun NetworkSignalDisplay(
    modifier: Modifier = Modifier,
    iconSize: androidx.compose.ui.unit.Dp = 48.dp
) {
    val context = LocalContext.current
    var signalLevel by remember { mutableStateOf(0) }
    var hasPermission by remember { mutableStateOf(false) }

    // Check permissions
    LaunchedEffect(Unit) {
        hasPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    // Monitor permission changes (re-check on resume/lifecycle if needed, but for now simple check is fine)
    // We do NOT request permissions here anymore, as it is handled by the OnboardingFlow.

    DisposableEffect(hasPermission) {
        if (hasPermission) {
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                val callback =
                    object : TelephonyCallback(), TelephonyCallback.SignalStrengthsListener {
                        override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
                            signalLevel = signalStrength.level // 0-4
                        }
                    }
                telephonyManager.registerTelephonyCallback(context.mainExecutor, callback)
                onDispose {
                    telephonyManager.unregisterTelephonyCallback(callback)
                }
            } else {
                @Suppress("DEPRECATION")
                val listener = object : android.telephony.PhoneStateListener() {
                    @Deprecated("Deprecated in Java")
                    override fun onSignalStrengthsChanged(signalStrength: SignalStrength?) {
                        super.onSignalStrengthsChanged(signalStrength)
                        signalLevel = signalStrength?.level ?: 0
                    }
                }
                @Suppress("DEPRECATION")
                telephonyManager.listen(
                    listener,
                    android.telephony.PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                )
                onDispose {
                    @Suppress("DEPRECATION")
                    telephonyManager.listen(
                        listener,
                        android.telephony.PhoneStateListener.LISTEN_NONE
                    )
                }
            }
        } else {
            onDispose { }
        }
    }

    val iconRes = when (signalLevel) {
        0 -> R.drawable.ic_signal_0
        1 -> R.drawable.ic_signal_1
        2 -> R.drawable.ic_signal_2
        3 -> R.drawable.ic_signal_3
        else -> R.drawable.ic_signal_4
    }

    Icon(
        painter = androidx.compose.ui.res.painterResource(id = iconRes),
        contentDescription = stringResource(id = R.string.signal_level_desc),
        tint = Color.LightGray,
        modifier = modifier.size(iconSize)
    )
}
