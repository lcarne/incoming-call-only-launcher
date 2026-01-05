package com.callonly.launcher.receivers

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent

class CallOnlyAdminReceiver : DeviceAdminReceiver() {
    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
    }
}
