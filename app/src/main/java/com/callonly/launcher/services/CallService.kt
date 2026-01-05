package com.callonly.launcher.services

import android.content.Intent
import android.telecom.Call
import android.telecom.InCallService
import com.callonly.launcher.manager.CallManager
import com.callonly.launcher.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CallService : InCallService() {

    @Inject
    lateinit var callManager: CallManager

    private val serviceScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main)

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        
        callManager.setCall(call)
        
        // Wait for CallManager to approve the call before showing UI/Notification
        serviceScope.launch {
            callManager.isCallAllowed.collect { allowed ->
                if (allowed) {
                    showIncomingCallNotification()
                }
            }
        }
    }

    private fun showIncomingCallNotification() {
        val channelId = "incoming_calls"
        val notificationManager = getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                channelId,
                "Incoming Calls",
                android.app.NotificationManager.IMPORTANCE_HIGH
            ).apply {
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = android.app.PendingIntent.getActivity(
            this, 
            0, 
            intent, 
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val notification = androidx.core.app.NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_menu_call)
            .setContentTitle("Appel entrant")
            .setContentText("Répondre ou décliner")
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_MAX)
            .setCategory(androidx.core.app.NotificationCompat.CATEGORY_CALL)
            .setFullScreenIntent(pendingIntent, true)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()

        notificationManager.notify(1, notification)
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        val notificationManager = getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.cancel(1)
        callManager.clear()
    }
}
