package com.basu.vaccineremainder.features.faq

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Uniform Color Palette ---
private val SlateDark = Color(0xFF556080)    // Premium Header
private val PrimaryIndigo = Color(0xFF4F46E5)
private val TextHead = Color(0xFF0F172A)
private val TextBody = Color(0xFF334155)
private val IconBgBlue = Color(0xFFE2E8F0)
private val SurfaceBg = Color(0xFFF1F5F9)

@Composable
fun FAQScreen(
    role: String, // "parent" or "provider"
    onBack: () -> Unit
) {
    val faqs = remember(role) { getFaqsForRole(role) }

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
                        imageVector = Icons.Outlined.HelpOutline,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Help & Support",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    Text(
                        text = "Frequently Asked Questions",
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(faqs) { faq ->
                    FAQItem(faq)
                }
            }
        }
    }
}

// --- Reusable Expandable Card ---
@Composable
fun FAQItem(faq: FAQ) {
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "rotation")

    Surface(
        onClick = { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 0.dp, // Flat style
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Question Icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(IconBgBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.QuestionAnswer,
                        contentDescription = null,
                        tint = PrimaryIndigo,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = faq.question,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextHead,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand",
                    tint = Color.Gray,
                    modifier = Modifier.rotate(rotationState)
                )
            }

            // Expandable Content
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = SurfaceBg)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = faq.answer,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextBody,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

