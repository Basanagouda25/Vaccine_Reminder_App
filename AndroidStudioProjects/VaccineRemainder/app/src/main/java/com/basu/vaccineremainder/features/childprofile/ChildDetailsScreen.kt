package com.basu.vaccineremainder.features.childprofile

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.basu.vaccineremainder.data.model.Child
import com.basu.vaccineremainder.data.repository.AppRepository
import com.basu.vaccineremainder.features.reports.generateChildReportPdf
import com.basu.vaccineremainder.features.reports.openPdf
import kotlinx.coroutines.launch

// --- Uniform Color Palette ---
private val SlateDark = Color(0xFF556080)    // Premium Header
private val PrimaryIndigo = Color(0xFF4F46E5)
private val TextHead = Color(0xFF0F172A)
private val TextLabel = Color(0xFF64748B)
private val IconBgBlue = Color(0xFFE2E8F0)
private val IconTintBlue = Color(0xFF64748B)
private val SurfaceBg = Color(0xFFF1F5F9)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChildDetailsScreen(
    repository: AppRepository,
    childId: Long,
    onBack: () -> Unit,
    onViewSchedule: (childId: Long) -> Unit
) {
    var child by remember { mutableStateOf<Child?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isGenerating by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch {
            // Fetch child details using the Long ID provided
            child = repository.getChildById(childId)
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
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Child Profile",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    Text(
                        text = "View and manage details",
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
                    .verticalScroll(rememberScrollState()) // Allow scrolling
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                if (child == null) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryIndigo)
                    }
                } else {
                    // Profile Hero Section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Large Avatar
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(IconBgBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Face,
                                    contentDescription = null,
                                    tint = IconTintBlue,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = child!!.name,
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextHead
                            )
                        }
                    }

                    // Info Grid
                    Text(
                        text = "Personal Information",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextHead,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    DetailRow(
                        icon = Icons.Default.CalendarToday,
                        label = "Date of Birth",
                        value = child!!.dateOfBirth
                    )

                    Divider(modifier = Modifier.padding(vertical = 16.dp), color = IconBgBlue)

                    DetailRow(
                        icon = Icons.Default.Face,
                        label = "Gender",
                        value = child!!.gender
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- Action Buttons ---

                    // 1. View Schedule Button
                    Button(
                        onClick = { onViewSchedule(childId) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryIndigo
                        )
                    ) {
                        Text(
                            text = "View Vaccination Schedule",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                isGenerating = true
                                try {
                                    val report = repository.buildChildReport(childId)
                                    Log.d("ReportDebug", "PDF REPORT about to generate:")
                                    report.vaccines.forEach {
                                        Log.d(
                                            "ReportDebug",
                                            "pdfRow: name=${it.name}, status=${it.status}, " +
                                                    "given=${it.dateGiven}, due=${it.dueDate}"
                                        )
                                    }

                                    val file = generateChildReportPdf(context, report)
                                    openPdf(context, file)
                                } catch (e: Exception) {
                                    Log.e("ReportDebug", "Failed to generate PDF", e)
                                } finally {
                                    isGenerating = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryIndigo
                        )
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Generating...",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Description,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Download Vaccine Report",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }


                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

// --- Reusable Detail Row Component ---
@Composable
fun DetailRow(icon: ImageVector, label: String, value: String) {
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