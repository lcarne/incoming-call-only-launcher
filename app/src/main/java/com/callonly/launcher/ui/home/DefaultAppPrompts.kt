package com.callonly.launcher.ui.home

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.callonly.launcher.R
import com.callonly.launcher.ui.theme.ErrorRed

@Composable
fun DefaultAppPrompts(
    isDefaultDialer: Boolean,
    isDefaultLauncher: Boolean
) {
    val context = LocalContext.current

    if (!isDefaultDialer) {
        val dialerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = { }
        )

        Button(
            onClick = {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    val roleManager =
                        context.getSystemService(Context.ROLE_SERVICE) as android.app.role.RoleManager
                    val intent =
                        roleManager.createRequestRoleIntent(android.app.role.RoleManager.ROLE_DIALER)
                    dialerLauncher.launch(intent)
                } else {
                    val intent =
                        Intent(android.telecom.TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).apply {
                            putExtra(
                                android.telecom.TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME,
                                context.packageName
                            )
                        }
                    dialerLauncher.launch(intent)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = ErrorRed
            ),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                stringResource(id = R.string.activate_calls),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    if (!isDefaultLauncher) {
        Button(
            onClick = {
                val intent = Intent(android.provider.Settings.ACTION_HOME_SETTINGS)
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                } else {
                    context.startActivity(Intent(android.provider.Settings.ACTION_SETTINGS))
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = ErrorRed
            ),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                stringResource(id = R.string.activate_launcher),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
