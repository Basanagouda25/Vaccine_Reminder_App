package com.basu.vaccineremainder.features

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState // Added for scroll state
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll // Added for scrolling
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SlateDark = Color(0xFF556080)
private val SurfaceBg = Color(0xFFF1F5F9)
private val TextHead = Color(0xFF0F172A)
private val TextLabel = Color(0xFF64748B)
private val PrimaryBlue = Color(0xFF2563EB)

@Composable
fun LearnScreen(onBack: () -> Unit) {
    // Initialize the scroll state
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // HEADER (Fixed at the top)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBack() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Educational Resources",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    Text(
                        text = "Learn about vaccines and your child’s health",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // SLIDING WHITE SURFACE (Scrollable Content)
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = SurfaceBg
        ) {
            // Apply verticalScroll and padding here
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState) // This enables scrolling
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                LearnCard(
                    icon = Icons.Default.MenuBook,
                    title = "Understanding Childhood Vaccines",
                    subtitle = "A comprehensive guide to all childhood vaccinations",
                    points = listOf(
                        "How vaccines work and why they are important",
                        "Overview of vaccines given from birth to 15 years",
                        "Protection against major diseases like measles & polio",
                        "How immunity is developed in children"
                    )
                )

                LearnCard(
                    icon = Icons.Default.HealthAndSafety,
                    title = "Vaccine Safety & Side Effects",
                    subtitle = "What to expect and when to contact your doctor",
                    points = listOf(
                        "Common mild reactions (fever, swelling)",
                        "Rare but serious side effects",
                        "When medical attention is required",
                        "Myths vs. facts about vaccine safety"
                    )
                )

                LearnCard(
                    icon = Icons.Default.Info,
                    title = "Vaccination Schedule Guide",
                    subtitle = "Complete timeline of recommended vaccines by age",
                    points = listOf(
                        "Birth to 6 months vaccination schedule",
                        "12–24 months booster guidance",
                        "5+ year vaccinations & school requirements",
                        "Why sticking to timeline is crucial"
                    )
                )

                // Extra spacer at bottom to ensure the last card isn't cut off by the screen edge
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun LearnCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector, // Changed Any to specific type for safety
    title: String,
    subtitle: String,
    points: List<String>
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp // Added slight elevation for better look on SurfaceBg
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryBlue.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = PrimaryBlue
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextHead
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextLabel
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            points.forEach { point ->
                Text(
                    text = "• $point",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextHead.copy(alpha = 0.85f),
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
        }
    }
}