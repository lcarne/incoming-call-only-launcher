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
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.callonly.launcher.ui.theme.StatusIcons
import com.callonly.launcher.ui.theme.LightGray
import com.callonly.launcher.ui.theme.White
import com.callonly.launcher.ui.theme.ErrorRed
import com.callonly.launcher.R

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
        val (color, iconVector) = when {
            level <= 20 -> ErrorRed to StatusIcons.BatteryAlert
            level <= 50 -> Color(0xFFFFC107) to StatusIcons.BatteryStd
            else -> Color(0xFF4CAF50) to StatusIcons.BatteryFull
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            Icon(
                imageVector = if (isCharging) StatusIcons.Charging else iconVector,
                contentDescription = stringResource(id = R.string.battery_level_desc),
                tint = if (isCharging) White else color,
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
                color = LightGray
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
        hasPermission = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    // Permission Launcher if needed (Automatic request on start)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasPermission = isGranted }
    )
    
    val permissionsToRequest = mutableListOf(
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.ANSWER_PHONE_CALLS,
        android.Manifest.permission.READ_CONTACTS,
        android.Manifest.permission.CALL_PHONE
    ).apply {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            add(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val multiplePermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { _ ->
            // Update hasPermission if needed, or just let it happen
        }
    )
    
    var showPermissionRationale by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val missingPermissions = permissionsToRequest.filter {
            ContextCompat.checkSelfPermission(context, it) != android.content.pm.PackageManager.PERMISSION_GRANTED
        }
        if (missingPermissions.isNotEmpty()) {
            multiplePermissionsLauncher.launch(missingPermissions.toTypedArray())
        }

        if (!hasPermission) {
             showPermissionRationale = true
        }
    }

    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { /* Prevent dismiss without action if critical, or allow */ },
            title = { Text(stringResource(id = R.string.location_permission_title)) },
            text = { Text(stringResource(id = R.string.location_permission_desc)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionRationale = false
                        permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                ) {
                    Text(stringResource(id = R.string.validate))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionRationale = false }) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }

    DisposableEffect(hasPermission) {
        if (hasPermission) {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                val callback = object : TelephonyCallback(), TelephonyCallback.SignalStrengthsListener {
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
                telephonyManager.listen(listener, android.telephony.PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)
                onDispose {
                    @Suppress("DEPRECATION")
                    telephonyManager.listen(listener, android.telephony.PhoneStateListener.LISTEN_NONE)
                }
            }
        } else {
             onDispose { }
        }
    }

    val icon = when (signalLevel) {
        0 -> StatusIcons.Signal0
        1 -> StatusIcons.Signal1
        2 -> StatusIcons.Signal2
        3 -> StatusIcons.Signal3
        else -> StatusIcons.Signal4
    }

    Icon(
        imageVector = icon,
        contentDescription = "Signal Level",
        tint = LightGray,
        modifier = modifier.size(iconSize)
    )
}
