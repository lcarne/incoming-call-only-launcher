package com.incomingcallonly.launcher.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@Composable
fun AppDialog(
    onDismissRequest: () -> Unit,
    title: String,
    buttons: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    message: String? = null,
    icon: ImageVector? = null,
    content: @Composable (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = icon?.let {
            {
                androidx.compose.material3.Icon(
                    imageVector = it,
                    contentDescription = null
                )
            }
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            if (content != null) {
                content()
            } else if (message != null) {
                Text(
                    text = parseBoldString(message),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = buttons,
        shape = RoundedCornerShape(24.dp),
        modifier = modifier
            .fillMaxWidth(0.95f)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                ambientColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            ),
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}

@Composable
fun parseBoldString(text: String): androidx.compose.ui.text.AnnotatedString {
    return buildAnnotatedString {
        val parts = text.split("*")
        parts.forEachIndexed { index, part ->
            if (index % 2 == 1) { // Odd indices are inside * *
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(part)
                }
            } else {
                append(part)
            }
        }
    }
}
