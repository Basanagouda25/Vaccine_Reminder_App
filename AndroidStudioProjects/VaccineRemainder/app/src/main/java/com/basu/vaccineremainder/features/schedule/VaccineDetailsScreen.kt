package com.basu.vaccineremainder.features.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.basu.vaccineremainder.data.model.Vaccine
import com.basu.vaccineremainder.data.repository.AppRepository
import kotlinx.coroutines.launch

// --- Uniform Color Palette ---
private val SlateDark = Color(0xFF556080)    // Premium Header
private val PrimaryIndigo = Color(0xFF4F46E5)
private val TextHead = Color(0xFF0F172A)
private val TextLabel = Color(0xFF64748B)
private val TextBody = Color(0xFF334155)
private val IconBgBlue = Color(0xFFE2E8F0)
private val IconTintBlue = Color(0xFF64748B)
private val SurfaceBg = Color(0xFFF1F5F9)

@Composable
fun VaccineDetailsScreen(
    repository: AppRepository,
    vaccineId: Int,
    onBack: () -> Unit
) {
    var vaccine by remember { mutableStateOf<Vaccine?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            vaccine = repository.getVaccineById(vaccineId)
        }
    }

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

            // Header Content
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MedicalServices,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Vaccine Info",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    Text(
                        text = "Detailed Overview",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- 2. Sliding Surface (Details Area) ---
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 32.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (vaccine == null) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryIndigo)
                    }
                } else {
                    // Hero Section (Icon + Name)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(IconBgBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Vaccines,
                                    contentDescription = null,
                                    tint = PrimaryIndigo,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = vaccine!!.vaccineName,
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextHead
                            )
                        }
                    }

                    // Info Section
                    Text(
                        text = "Specification",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextHead,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    VaccineDetailRow(
                        icon = Icons.Default.CalendarToday,
                        label = "Recommended Age",
                        value = "${vaccine!!.recommendedAgeMonths} months"
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(color = SurfaceBg)
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "About",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextHead,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Description Box
                    Surface(
                        color = SurfaceBg,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = vaccine!!.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextBody,
                            lineHeight = 24.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                }
            }
        }
    }
}

// --- Reusable Detail Row ---
@Composable
fun VaccineDetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SurfaceBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryIndigo,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = TextLabel
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = TextHead
            )
        }
    }
}