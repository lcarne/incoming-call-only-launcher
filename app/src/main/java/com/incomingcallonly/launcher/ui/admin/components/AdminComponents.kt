package com.incomingcallonly.launcher.ui.admin.components


import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.incomingcallonly.launcher.R
import com.incomingcallonly.launcher.ui.components.DepthIcon
import com.incomingcallonly.launcher.ui.theme.IncomingCallOnlyTheme
import com.incomingcallonly.launcher.ui.theme.Spacing


// ============================================================================
// SELECTION DIALOGS
// ============================================================================

@Composable
fun <T> AdminSelectionDialog(
    title: String,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    onDismissRequest: () -> Unit,
    labelProvider: @Composable (T) -> String,
    modifier: Modifier = Modifier,
    iconProvider: (@Composable (T) -> Unit)? = null,
    headerIcon: Any? = null,
) {
    AdminDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        modifier = modifier,
        icon = headerIcon,
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                val isSelected = option == selectedOption
                
                // Only create the leadingIcon lambda if iconProvider is not null
                val leadingIconLambda: (@Composable () -> Unit)? = if (iconProvider != null) {
                    { iconProvider(option) }
                } else null

                AdminSelectionItem(
                    text = labelProvider(option),
                    isSelected = isSelected,
                    onClick = { onOptionSelected(option) },
                    leadingIcon = leadingIconLambda
                )
            }
        }
    }
}

@Composable
fun AdminSelectionItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val backgroundColor = if (isSelected) 
        MaterialTheme.colorScheme.primaryContainer 
    else 
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        
    val contentColor = if (isSelected) 
        MaterialTheme.colorScheme.onPrimaryContainer 
    else 
        MaterialTheme.colorScheme.onSurface

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        tonalElevation = if (isSelected) 2.dp else 0.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(12.dp))
            }
            
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = contentColor,
                modifier = Modifier.weight(1f)
            )
            
            if (isSelected) {
                DepthIcon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ============================================================================
// CARDS & CONTAINERS
// ============================================================================

@Composable
fun AdminSettingsCard(
    modifier: Modifier = Modifier,
    containerColor: Color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else Color.White,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.xs)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color.Black.copy(alpha = 0.15f),
                ambientColor = Color.Black.copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = Spacing.sm)
        ) {
            content()
        }
    }
}

@Composable
fun AdminNavigationItem(
    headlineText: String,
    leadingIcon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color.Black.copy(alpha = 0.15f),
                ambientColor = Color.Black.copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
        )
    ) {
        ListItem(
            headlineContent = {
                Text(
                    headlineText,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            supportingContent = supportingText?.let {
                { Text(it, style = MaterialTheme.typography.bodyMedium) }
            },
            leadingContent = leadingIcon,
            trailingContent = trailingIcon,
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            modifier = Modifier.padding(vertical = Spacing.sm)
        )
    }
}

// ============================================================================
// HEADERS & DIVIDERS
// ============================================================================

@Composable
fun AdminSectionHeader(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(
                start = Spacing.md,
                top = Spacing.sectionHeaderTop,
                bottom = Spacing.sectionHeaderBottom
            )
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            shape = RoundedCornerShape(100),
            modifier = Modifier.height(32.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = text.uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun AdminDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier
            .padding(vertical = Spacing.xs)
            .padding(horizontal = Spacing.listItemHorizontal),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
    )
}

// ============================================================================
// DIALOGS
// ============================================================================

@Composable
fun AdminDialog(
    onDismissRequest: () -> Unit,
    title: String,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    icon: Any? = null,
    iconContainerColor: Color? = null,
    iconTint: Color? = null,
    animated: Boolean = true,
    dismissButton: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        var animateTrigger by remember { mutableStateOf(!animated) }
        
        if (animated) {
            LaunchedEffect(Unit) {
                animateTrigger = true
            }
        }

        val alpha by animateFloatAsState(
            targetValue = if (animated && !animateTrigger) 0f else 1f,
            animationSpec = spring(stiffness = Spring.StiffnessLow),
            label = "dialogAlpha"
        )
        
        val offsetY by animateFloatAsState(
            targetValue = if (animated && !animateTrigger) 100f else 0f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
            label = "dialogOffset"
        )

        IncomingCallOnlyTheme {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(Spacing.sm)
                    .graphicsLayer {
                        this.alpha = alpha
                        this.translationY = offsetY
                    }
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(24.dp),
                        spotColor = Color(0x40000000),
                        ambientColor = Color(0x20000000)
                    )
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                val isDark = isSystemInDarkTheme()
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    if (isDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.8f),
                                    if (isDark) Color.White.copy(alpha = 0.02f) else Color.White.copy(alpha = 0.4f)
                                )
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                )

                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (icon != null) {
                        val containerColor = iconContainerColor ?: MaterialTheme.colorScheme.primaryContainer
                        val tintColor = iconTint ?: MaterialTheme.colorScheme.onSecondaryContainer
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = containerColor,
                            modifier = Modifier
                                .size(64.dp)
                                .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                when (icon) {
                                    is ImageVector -> DepthIcon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp),
                                        tint = tintColor
                                    )
                                    is Int -> DepthIcon(
                                        painter = painterResource(id = icon),
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp),
                                        tint = tintColor
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        content()
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (dismissButton != null) {
                            Box(modifier = Modifier.weight(1f)) {
                                dismissButton()
                            }
                        }
                        
                        Box(modifier = Modifier.weight(1f)) {
                             confirmButton()
                        }
                    }
                }
            }
        }
    }
}



// ============================================================================
// ICONS
// ============================================================================

@Composable
fun AdminIcon(
    painter: Painter,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    containerColor: Color = Color.Transparent
) {
    if (containerColor != Color.Transparent) {
        Box(
            modifier = modifier
                .size(Spacing.iconLarge + 16.dp)
                .background(containerColor, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            DepthIcon(
                painter = painter,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(Spacing.iconLarge)
            )
        }
    } else {
        DepthIcon(
            painter = painter,
            contentDescription = null,
            tint = tint,
            modifier = modifier.size(Spacing.iconLarge)
        )
    }
}

@Composable
fun AdminIcon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    containerColor: Color = Color.Transparent
) {
    if (containerColor != Color.Transparent) {
        Box(
            modifier = modifier
                .size(Spacing.iconLarge + 16.dp)
                .background(containerColor, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            DepthIcon(
                imageVector = imageVector,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(Spacing.iconLarge)
            )
        }
    } else {
        DepthIcon(
            imageVector = imageVector,
            contentDescription = null,
            tint = tint,
            modifier = modifier.size(Spacing.iconLarge)
        )
    }
}



// ============================================================================
// TOGGLES
// ============================================================================

@Composable
fun AdminSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val scale by animateFloatAsState(
        targetValue = if (checked) 1.1f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "switchScale"
    )
    
    Box(modifier = modifier.scale(scale)) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                checkedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                uncheckedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        )
    }
}

// ============================================================================
// BUTTONS
// ============================================================================

@Composable
fun ModernSegmentedButton(
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .heightIn(min = 52.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            options.forEachIndexed { index, option ->
                val isSelected = index == selectedIndex
                
                val backgroundColor = if (isSelected) 
                    MaterialTheme.colorScheme.surface 
                else 
                    Color.Transparent
                
                val contentColor = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)

                val shadowElevation by animateFloatAsState(
                    targetValue = if (isSelected) 3f else 0f,
                    label = "elevation"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .shadow(
                            elevation = if (isSelected) shadowElevation.dp else 0.dp,
                            shape = RoundedCornerShape(12.dp),
                            spotColor = Color.Black.copy(alpha = 0.15f)
                        )
                        .background(backgroundColor, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onOptionSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            letterSpacing = 0.5.sp
                        ),
                        color = contentColor,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun AdminLargeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.97f else 1.0f, label = "scale")
    val elevation by animateFloatAsState(targetValue = if (isPressed) 2.0f else 8.0f, label = "elevation")

    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .padding(horizontal = Spacing.md, vertical = Spacing.xs)
            .shadow(
                elevation = elevation.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = containerColor.copy(alpha = 0.5f),
                ambientColor = Color.Black.copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        contentColor = contentColor,
        interactionSource = interactionSource
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                DepthIcon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            )
        }
    }
}

@Composable
fun AdminDangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AdminWarningText(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center
        )
    }
}
