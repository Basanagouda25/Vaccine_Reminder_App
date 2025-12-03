package com.basu.vaccineremainder.features.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.basu.vaccineremainder.data.model.Vaccine
import com.basu.vaccineremainder.data.repository.AppRepository
import kotlinx.coroutines.launch

// --- Uniform Color Palette ---
private val SlateDark = Color(0xFF556080)    // Premium Header
private val TextHead = Color(0xFF0F172A)
private val TextLabel = Color(0xFF334155)
private val TextGrey = Color(0xFF64748B)
private val IconBgBlue = Color(0xFFE2E8F0)
private val IconTintBlue = Color(0xFF64748B)
private val SurfaceBg = Color(0xFFF1F5F9)

@Composable
fun VaccineListScreen(
    repository: AppRepository,
    onVaccineSelected: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    var vaccineList by remember { mutableStateOf<List<Vaccine>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            vaccineList = repository.getAllVaccines()
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
                    .clickable { onNavigateBack() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Header Content
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
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
                        text = "Vaccine Database",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    Text(
                        text = "Standard Immunization List",
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
                if (vaccineList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = SlateDark)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(vaccineList) { vaccine ->
                            VaccineListItem(vaccine = vaccine, onVaccineSelected = onVaccineSelected)
                        }
                    }
                }
            }
        }
    }
}

// --- Reusable Vaccine Card ---
@Composable
fun VaccineListItem(vaccine: Vaccine, onVaccineSelected: (Int) -> Unit) {
    Surface(
        onClick = { onVaccineSelected(vaccine.vaccineId) },
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 0.dp, // Flat modern style
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(IconBgBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Vaccines,
                    contentDescription = null,
                    tint = IconTintBlue,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = vaccine.vaccineName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextHead
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Recommended Age: ${vaccine.recommendedAgeMonths} months",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGrey
                )
            }

            // Arrow
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Details",
                tint = TextGrey.copy(alpha = 0.5f)
            )
        }
    }
}