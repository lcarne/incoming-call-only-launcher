package com.incomingcallonly.launcher.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter

@Composable
fun DepthIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    shadowColor: Color = Color.Black.copy(alpha = 0.15f),
    shadowOffset: Offset = Offset(4f, 4f)
) {
    DepthIcon(
        painter = rememberVectorPainter(imageVector),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
        shadowColor = shadowColor,
        shadowOffset = shadowOffset
    )
}

@Composable
fun DepthIcon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    shadowColor: Color = Color.Black.copy(alpha = 0.15f),
    shadowOffset: Offset = Offset(4f, 4f)
) {
    Icon(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint
    )
}
