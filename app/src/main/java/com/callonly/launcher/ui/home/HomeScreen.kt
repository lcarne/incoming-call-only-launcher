package com.callonly.launcher.ui.home

import androidx.compose.material.icons.filled.Settings
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.callonly.launcher.data.model.Contact
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

import android.telephony.SignalStrength
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.ui.graphics.toArgb
import com.callonly.launcher.ui.theme.HighContrastButtonBg
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAdminClick: () -> Unit
) {
    // Clock State
    var currentTime by remember { mutableStateOf(Date()) }
    
    // Settings State
    val isAlwaysOn by viewModel.isAlwaysOnEnabled.collectAsState()
    val nightStart by viewModel.nightModeStartHour.collectAsState()
    val nightEnd by viewModel.nightModeEndHour.collectAsState()
    val savedClockColor by viewModel.clockColor.collectAsState()

    val clockColor = if (savedClockColor != 0) Color(savedClockColor) else HighContrastButtonBg
    
    val context = LocalContext.current
    // Contacts removed from Home Screen as requested
    
    // Screen Keep On Logic
    // Helper to find Activity from Context (handles Hilt/Compose wrappers)
    fun android.content.Context.findActivity(): android.app.Activity? {
        var currentContext = this
        while (currentContext is android.content.ContextWrapper) {
            if (currentContext is android.app.Activity) return currentContext
            currentContext = currentContext.baseContext
        }
        return null
    }

    LaunchedEffect(isAlwaysOn) {
        val activity = context.findActivity()
        if (activity != null) {
            if (isAlwaysOn) {
                activity.window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                activity.window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }
    
    // Fake Sleep Logic
    val calendar = java.util.Calendar.getInstance()
    calendar.time = currentTime
    val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
    val isNight = if (nightStart < nightEnd) {
        hour in nightStart until nightEnd
    } else {
        hour >= nightStart || hour < nightEnd
    }
    
    var isTemporarilyAwake by remember { mutableStateOf(false) }
    
    LaunchedEffect(isNight, isTemporarilyAwake) {
        if (isTemporarilyAwake) {
            delay(30_000) // 30 seconds awake then back to sleep
            isTemporarilyAwake = false
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Date()
            delay(1000)
        }
    }

    val timeFormatPattern = remember {
        mutableStateOf("HH:mm")
    }
    val savedFormat by viewModel.timeFormat.collectAsState()
    LaunchedEffect(savedFormat) {
        timeFormatPattern.value = if (savedFormat == "12") "hh:mm a" else "HH:mm"
    }
    val timeFormat = SimpleDateFormat(timeFormatPattern.value, Locale.getDefault())
    val dateFormat = SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault())

    // High Contrast / Black Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(com.callonly.launcher.ui.theme.Black),
        contentAlignment = Alignment.Center
    ) {
        // Fake Sleep Overlay (Black Screen that handles Tap to Wake)
        if (isAlwaysOn && isNight && !isTemporarilyAwake) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .zIndex(100f)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { isTemporarilyAwake = true }
                        )
                    }
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Battery and Network
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                 NetworkSignalDisplay()
                 Spacer(modifier = Modifier.width(32.dp))
                 BatteryLevelDisplay()
            }
            
            // Date
            val rawDate = dateFormat.format(currentTime)
            val capitalizedDate = rawDate.split(" ").joinToString(" ") { 
                if (it.firstOrNull()?.isLetter() == true) it.replaceFirstChar { char -> char.uppercase() } else it 
            }

            Text(
                text = capitalizedDate,
                style = MaterialTheme.typography.displayMedium.copy(fontSize = 30.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                color = com.callonly.launcher.ui.theme.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            try {
                                // Long press of 30 seconds to enter Admin
                                withTimeout(30000) {
                                    tryAwaitRelease()
                                }
                            } catch (e: TimeoutCancellationException) {
                                onAdminClick()
                            }
                        }
                    )
                }
            )

            if (savedFormat == "12") {
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
                    text = timeFormat.format(currentTime),
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 120.sp),
                    color = clockColor,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // Ringer Toggle Button
            val isRingerEnabled by viewModel.isRingerEnabled.collectAsState()
            
            androidx.compose.material3.FilledTonalButton(
                onClick = { viewModel.setRingerEnabled(!isRingerEnabled) },
                modifier = Modifier
                    .width(300.dp)
                    .height(100.dp),
                colors = androidx.compose.material3.ButtonDefaults.filledTonalButtonColors(
                    containerColor = if (isRingerEnabled) clockColor else com.callonly.launcher.ui.theme.ErrorRed,
                    contentColor = if (isRingerEnabled) com.callonly.launcher.ui.theme.Black else com.callonly.launcher.ui.theme.White
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (isRingerEnabled) com.callonly.launcher.ui.theme.StatusIcons.VolumeUp else com.callonly.launcher.ui.theme.StatusIcons.VolumeOff,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isRingerEnabled) stringResource(id = com.callonly.launcher.R.string.ringer_active) else stringResource(id = com.callonly.launcher.R.string.ringer_disabled),
                        fontSize = 20.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Default Dialer Check
            val roleManager = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                context.getSystemService(Context.ROLE_SERVICE) as android.app.role.RoleManager
            } else null

            var isDefaultDialer by remember { mutableStateOf(true) }
            
            LaunchedEffect(Unit) {
                while(true) {
                    isDefaultDialer = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        roleManager?.isRoleHeld(android.app.role.RoleManager.ROLE_DIALER) == true
                    } else {
                        val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as android.telecom.TelecomManager
                        telecomManager.defaultDialerPackage == context.packageName
                    }
                    delay(5000) // Check every 5 seconds is enough
                }
            }

            if (!isDefaultDialer) {
                val dialerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult(),
                    onResult = { }
                )

                Button(
                    onClick = {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            val intent = roleManager?.createRequestRoleIntent(android.app.role.RoleManager.ROLE_DIALER)
                            if (intent != null) dialerLauncher.launch(intent)
                        } else {
                            val intent = Intent(android.telecom.TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).apply {
                                putExtra(android.telecom.TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, context.packageName)
                            }
                            dialerLauncher.launch(intent)
                        }
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = com.callonly.launcher.ui.theme.ErrorRed),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(stringResource(id = com.callonly.launcher.R.string.activate_calls), fontSize = 24.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ContactCard(
    contact: Contact,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (contact.photoUri != null) {
                AsyncImage(
                    model = contact.photoUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Overlay for text legibility
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(androidx.compose.ui.graphics.Color.Transparent, androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.7f))
                        ))
                )
            }
            
            Text(
                text = contact.name,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun BatteryLevelDisplay() {
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
            level <= 20 -> com.callonly.launcher.ui.theme.ErrorRed to com.callonly.launcher.ui.theme.StatusIcons.BatteryAlert
            level <= 50 -> androidx.compose.ui.graphics.Color(0xFFFFC107) to com.callonly.launcher.ui.theme.StatusIcons.BatteryStd
            else -> androidx.compose.ui.graphics.Color(0xFF4CAF50) to com.callonly.launcher.ui.theme.StatusIcons.BatteryFull
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
                Icon(
                imageVector = if (isCharging) com.callonly.launcher.ui.theme.StatusIcons.Charging else iconVector,
                contentDescription = stringResource(id = com.callonly.launcher.R.string.battery_level_desc),
                tint = if (isCharging) com.callonly.launcher.ui.theme.White else color,
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 8.dp)
            )
            Text(
                text = "$level%",
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 32.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                color = com.callonly.launcher.ui.theme.LightGray
            )
        }
    }
}

@Composable
fun NetworkSignalDisplay() {
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
    
    LaunchedEffect(Unit) {
        val missingPermissions = permissionsToRequest.filter {
            ContextCompat.checkSelfPermission(context, it) != android.content.pm.PackageManager.PERMISSION_GRANTED
        }
        if (missingPermissions.isNotEmpty()) {
            multiplePermissionsLauncher.launch(missingPermissions.toTypedArray())
        }

        if (!hasPermission) {
             permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
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
        0 -> com.callonly.launcher.ui.theme.StatusIcons.Signal0
        1 -> com.callonly.launcher.ui.theme.StatusIcons.Signal1
        2 -> com.callonly.launcher.ui.theme.StatusIcons.Signal2
        3 -> com.callonly.launcher.ui.theme.StatusIcons.Signal3
        else -> com.callonly.launcher.ui.theme.StatusIcons.Signal4
    }

    Icon(
        imageVector = icon,
        contentDescription = "Signal Level",
        tint = com.callonly.launcher.ui.theme.LightGray,
        modifier = Modifier.size(48.dp)
    )
}
