package com.basu.vaccineremainder.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Color Palette from Design ---
val Indigo600 = Color(0xFF4F46E5)
val Violet700 = Color(0xFF6D28D9)
val TextWhite = Color(0xFFFFFFFF)
val TextGreyLight = Color(0xFFE2E8F0)
val CardParentBg = Color(0xFFFFFFFF)
val CardProviderBg = Color(0xFF4338CA).copy(alpha = 0.6f) // Semi-transparent Indigo

@Composable
fun RoleSelectionScreen(
    onUserClick: () -> Unit = {},
    onProviderClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    // Main Container with Gradient Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Indigo600, Violet700),
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(0f, Float.POSITIVE_INFINITY)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // --- 1. Logo Section ---
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.15f)), // Glassmorphism effect
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = "Logo",
                    tint = TextWhite,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "VaxTracker",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    letterSpacing = (-0.5).sp
                ),
                color = TextWhite
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Secure vaccine management for families\nand providers.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 20.sp
                ),
                color = TextGreyLight,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // --- 2. Role Cards ---

            // Parent Card (White Background)
            RoleCard(
                title = "I am a Parent",
                subtitle = "Track my child",
                icon = Icons.Outlined.Face,
                backgroundColor = CardParentBg,
                textColor = Color(0xFF1E293B), // Slate 800
                subtitleColor = Color(0xFF64748B), // Slate 500
                iconBgColor = Color(0xFFEEF2FF), // Indigo 50
                iconTint = Indigo600,
                onClick = onUserClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Provider Card (Dark/Transparent Background)
            RoleCard(
                title = "Health Provider",
                subtitle = "Manage patients",
                icon = Icons.Filled.MedicalServices,
                backgroundColor = CardProviderBg, // Darker, semi-transparent
                textColor = TextWhite,
                subtitleColor = TextGreyLight.copy(alpha = 0.8f),
                iconBgColor = Color.White.copy(alpha = 0.1f),
                iconTint = TextWhite,
                onClick = onProviderClick
            )

            Spacer(modifier = Modifier.weight(1f))

        }
    }
}

// --- Reusable Component for the Role Buttons ---
@Composable
fun RoleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    backgroundColor: Color,
    textColor: Color,
    subtitleColor: Color,
    iconBgColor: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(88.dp),
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        shadowElevation = 4.dp // Adds subtle depth
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Container
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text Column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = textColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = subtitleColor
                )
            }

            // Arrow
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = subtitleColor.copy(alpha = 0.5f)
            )
        }
    }
}

@Preview
@Composable
fun PreviewRoleScreen() {
    RoleSelectionScreen()
}