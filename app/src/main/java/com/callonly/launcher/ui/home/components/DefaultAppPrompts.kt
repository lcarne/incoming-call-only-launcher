package com.callonly.launcher.ui.home.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.callonly.launcher.R
import com.callonly.launcher.ui.theme.Red

@Composable
fun DefaultAppPrompts(
    isDefaultDialer: Boolean,
    isDefaultLauncher: Boolean
) {
    if (!isDefaultDialer) {
        Text(
            text = stringResource(R.string.warning_not_dialer),
            style = MaterialTheme.typography.bodyLarge,
            color = Red,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }

    if (!isDefaultLauncher) {
        Text(
            text = stringResource(R.string.warning_not_launcher),
            style = MaterialTheme.typography.bodyLarge,
            color = Red,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}
