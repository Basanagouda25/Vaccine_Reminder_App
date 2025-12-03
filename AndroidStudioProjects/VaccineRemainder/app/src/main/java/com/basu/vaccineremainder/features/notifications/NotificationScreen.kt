package com.basu.vaccineremainder.features.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basu.vaccineremainder.data.model.AppNotification
import com.basu.vaccineremainder.data.repository.AppRepository
import com.basu.vaccineremainder.util.SessionManager

// --- Uniform Color Palette ---
private val SlateDark = Color(0xFF556080)    // Premium Header
private val TextHead = Color(0xFF0F172A)
private val TextBody = Color(0xFF334155)
private val TextDate = Color(0xFF94A3B8)
private val IconBgBlue = Color(0xFFE2E8F0)
private val IconTintBlue = Color(0xFF64748B)
private val SurfaceBg = Color(0xFFF1F5F9)

@Composable
fun NotificationScreen(
    repository: AppRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val parentEmail = remember {
        SessionManager.getParentEmail(context) ?: ""
    }

    LaunchedEffect(Unit) {
        println("🔍 NotificationScreen STARTED, parentEmail='$parentEmail'")
    }

    // 🔹 Get ViewModel using factory
    val viewModel: NotificationViewModel = viewModel(
        factory = NotificationViewModelFactory(repository, parentEmail)
    )

    // 🔹 Collect StateFlow
    val notifications by viewModel.notifications.collectAsState()

    // --- Root Container (Dark Background) ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark)
    ) {

        // --- 1. Header Section ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            // Back Button
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBack() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Icon + Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.NotificationsNone,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Notifications",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    Text(
                        text = "Alerts & Updates",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- 2. Sliding Surface (List Area) ---
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = SurfaceBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                // Debug / Info Text (Styled minimally)
                if (parentEmail.isNotBlank()) {
                    Text(
                        text = "Inbox for: $parentEmail",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextDate,
                        modifier = Modifier.padding(bottom = 16.dp, start = 4.dp)
                    )
                }

                if (notifications.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = Color.Gray.copy(alpha = 0.3f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "You have no notifications.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextDate
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(notifications) { notification ->
                            NotificationCard(notification)
                        }
                    }
                }
            }
        }
    }
}

// --- Reusable Notification Card ---
@Composable
private fun NotificationCard(notification: AppNotification) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 0.dp, // Flat modern style
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Icon Indicator
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(IconBgBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = IconTintBlue,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text Content
            Column {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextHead
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextBody,
                    lineHeight = 20.sp
                )

                // Add timestamp if available in your model, else omit or add placeholder logic
                /*
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Just now",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDate
                )
                */
            }
        }
    }
}