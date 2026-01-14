package com.incomingcallonly.launcher.ui.components

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
import com.incomingcallonly.launcher.R
import com.incomingcallonly.launcher.ui.theme.ErrorRed
import com.incomingcallonly.launcher.ui.theme.White

private const val BATTERY_LOW_THRESHOLD = 20
private const val BATTERY_MEDIUM_THRESHOLD = 50

private val COLOR_BATTERY_LOW = Color(0xFFF44336) // Red
private val COLOR_BATTERY_MEDIUM = Color(0xFFFFEB3B) // Yellow
private val COLOR_BATTERY_HIGH = Color(0xFF4CAF50) // Green

private val COLOR_BATTERY_CHARGING = Color.White // White for charging bolt/fill

private const val BODY_WIDTH_RATIO = 0.6f
private const val BODY_HEIGHT_RATIO = 0.75f
private const val CAP_WIDTH_RATIO_OF_BODY = 0.5f
private const val CAP_HEIGHT_RATIO = 0.08f

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

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            BatteryIcon(
                level = level,
                isCharging = isCharging,
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
fun BatteryIcon(
    level: Int,
    isCharging: Boolean,
    modifier: Modifier = Modifier
) {
    val levelColor = when {
        level <= BATTERY_LOW_THRESHOLD -> COLOR_BATTERY_LOW
        level <= BATTERY_MEDIUM_THRESHOLD -> COLOR_BATTERY_MEDIUM
        else -> COLOR_BATTERY_HIGH
    }

    androidx.compose.foundation.Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        
        // Battery Dimensions (Vertical)
        val bodyWidth = width * BODY_WIDTH_RATIO
        val bodyHeight = height * BODY_HEIGHT_RATIO
        val capWidth = bodyWidth * CAP_WIDTH_RATIO_OF_BODY
        val capHeight = height * CAP_HEIGHT_RATIO
        val bodyLeft = (width - bodyWidth) / 2
        val totalHeight = bodyHeight + capHeight
        val startY = (height - totalHeight) / 2
        val bodyTopFinal = startY + capHeight
        
        // Draw Cap
        drawRect(
            color = Color.LightGray,
            topLeft = androidx.compose.ui.geometry.Offset(
                x = (width - capWidth) / 2,
                y = startY
            ),
            size = androidx.compose.ui.geometry.Size(capWidth, capHeight)
        )
        
        // Draw Body Outline
        drawRoundRect(
            color = Color.LightGray,
            topLeft = androidx.compose.ui.geometry.Offset(bodyLeft, bodyTopFinal),
            size = androidx.compose.ui.geometry.Size(bodyWidth, bodyHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx()),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
        )
        
        // Draw Fill Level
        // Calculate fill height based on level
        // Padding for outline stroke
        val strokePadding = 4.dp.toPx() 
        val fillMaxWidth = bodyWidth - strokePadding * 2
        val fillMaxHeight = bodyHeight - strokePadding * 2
        
        val fillHeight = (fillMaxHeight * (level / 100f)).coerceIn(0f, fillMaxHeight)
        
        if (fillHeight > 0) {
            drawRoundRect(
                color = if (isCharging) COLOR_BATTERY_CHARGING else levelColor,
                topLeft = androidx.compose.ui.geometry.Offset(
                    x = bodyLeft + strokePadding,
                    y = bodyTopFinal + strokePadding + (fillMaxHeight - fillHeight)
                ),
                size = androidx.compose.ui.geometry.Size(fillMaxWidth, fillHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )
        }
        
        // Draw Bolt if charging
        if (isCharging) {
             // A simple bolt shape
             val boltPath = androidx.compose.ui.graphics.Path().apply {
                 val cx = width / 2
                 val cy = bodyTopFinal + bodyHeight / 2
                 // Simplified bolt coords relative to center
                 moveTo(cx + bodyWidth * 0.1f, cy - bodyHeight * 0.15f)
                 lineTo(cx - bodyWidth * 0.2f, cy + bodyHeight * 0.05f)
                 lineTo(cx - bodyWidth * 0.05f, cy + bodyHeight * 0.05f)
                 lineTo(cx - bodyWidth * 0.1f, cy + bodyHeight * 0.2f) // Point
                 lineTo(cx + bodyWidth * 0.2f, cy - bodyHeight * 0.05f)
                 lineTo(cx + bodyWidth * 0.05f, cy - bodyHeight * 0.05f)
                 close()
             }
             drawPath(
                 path = boltPath,
                 color = Color.Black
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
