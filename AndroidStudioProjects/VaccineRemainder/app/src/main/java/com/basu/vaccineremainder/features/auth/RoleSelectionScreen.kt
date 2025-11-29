package com.basu.vaccineremainder.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

// --- Uniform Color Palette (Matches ProviderDashboard) ---
private val SlateDark = Color(0xFF556080)    // Premium Dark Background
private val SurfaceWhite = Color(0xFFFFFFFF) // Card Background
private val TextDark = Color(0xFF334155)     // Slate 700 for Card Titles
private val TextGrey = Color(0xFF64748B)     // Slate 500 for Subtitles
private val IconBgBlue = Color(0xFFE2E8F0)   // Light Blue-Grey for Icons
private val IconTintBlue = Color(0xFF64748B) // Darker Blue-Grey for Icons

@Composable
fun RoleSelectionScreen(
    onUserClick: () -> Unit = {},
    onProviderClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    // 1. Premium Dark Background
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // --- Logo Section ---
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White.copy(alpha = 0.1f)), // Subtle Glass effect
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Shield,
                contentDescription = "Logo",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "VaxTracker",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                letterSpacing = (-0.5).sp
            ),
            color = Color.White
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Secure vaccine management for families\nand providers.",
            style = MaterialTheme.typography.bodyMedium.copy(
                lineHeight = 24.sp,
                fontSize = 16.sp
            ),
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(56.dp))

        // --- Role Cards (Clean White Style) ---

        // Parent Card
        RoleCard(
            title = "I am a Parent",
            subtitle = "Track my child",
            icon = Icons.Outlined.Face,
            onClick = onUserClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Provider Card
        RoleCard(
            title = "Health Provider",
            subtitle = "Manage patients",
            icon = Icons.Filled.MedicalServices,
            onClick = onProviderClick
        )

        Spacer(modifier = Modifier.weight(1f))

        // --- Footer ---
        TextButton(onClick = onLoginClick) {
            Text(
                text = buildAnnotatedString {
                    append("Already have an account? ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)) {
                        append("Log in")
                    }
                },
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// --- Reusable Component matching Dashboard 'ActionCard' style ---
@Composable
fun RoleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(96.dp),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceWhite,
        shadowElevation = 0.dp // Flat modern style
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Container (Matches Dashboard Grid Items)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(IconBgBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = IconTintBlue,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Text Column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGrey
                )
            }

            // Arrow
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextGrey.copy(alpha = 0.5f)
            )
        }
    }
}

@Preview
@Composable
fun PreviewRoleScreen() {
    RoleSelectionScreen()
}