package com.basu.vaccineremainder.features.schedule

import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.EventNote
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
import com.basu.vaccineremainder.data.model.Schedule
import com.basu.vaccineremainder.data.repository.AppRepository
import java.util.Calendar

// --- Uniform Color Palette ---
private val SlateDark = Color(0xFF556080)    // Premium Header
private val PrimaryIndigo = Color(0xFF4F46E5)
private val TextHead = Color(0xFF0F172A)
private val TextLabel = Color(0xFF64748B)
private val SurfaceBg = Color(0xFFF1F5F9)

// Status Colors
private val ColorUpcoming = Color(0xFF0284C7) // Sky Blue
private val ColorMissed = Color(0xFFEF4444)   // Red
private val ColorCompleted = Color(0xFF10B981) // Emerald Green

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChildScheduleScreen(
    repository: AppRepository,
    childId: Long,
    onBack: () -> Unit
) {
    // ViewModel Setup
    val viewModel: ChildScheduleViewModel = viewModel(
        factory = ChildScheduleViewModelFactory(repository)
    )

    val upcoming by viewModel.upcoming.collectAsState()
    val completed by viewModel.completed.collectAsState()
    val missed by viewModel.missed.collectAsState()

    var selectedSchedule by remember { mutableStateOf<Schedule?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // --- DATE PICKER DIALOG ---
    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val formattedDate = "%04d-%02d-%02d".format(year, month + 1, day)
                selectedSchedule?.let {
                    viewModel.reschedule(it.scheduleId, formattedDate)
                }
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setOnDismissListener { showDatePicker = false }
            show()
        }
    }

    // Load Data
    LaunchedEffect(childId) {
        viewModel.detectAndMarkMissed(childId)
        viewModel.loadSchedules(childId)
    }

    // --- Root Container (Dark Background) ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

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
                        imageVector = Icons.Outlined.EventNote,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = "Vaccine Schedule",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    Text(
                        text = "Timeline & Status",
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

                // --- MISSED SECTION (Priority) ---
                if (missed.isNotEmpty()) {
                    item { SectionHeader("Missed / Overdue", ColorMissed) }
                    items(missed) { s ->
                        ScheduleCard(
                            schedule = s,
                            repository = repository,
                            statusColor = ColorMissed,
                            onMarkCompleted = {
                                // This should mark it as COMPLETED, not MISSED again
                                viewModel.markScheduleCompleted(s.scheduleId)
                            },
                            onReschedule = {
                                selectedSchedule = s
                                showDatePicker = true
                            }
                        )
                    }
                }


                // --- UPCOMING SECTION ---
                if (upcoming.isNotEmpty()) {
                    item { SectionHeader("Upcoming", ColorUpcoming) }
                    items(upcoming) { s ->
                        ScheduleCard(
                            schedule = s,
                            repository = repository,
                            statusColor = ColorUpcoming,
                            onMarkCompleted = { viewModel.markScheduleCompleted(s.scheduleId) },
                            onReschedule = { selectedSchedule = s; showDatePicker = true }
                        )
                    }
                }

                // --- COMPLETED SECTION ---
                if (completed.isNotEmpty()) {
                    item { SectionHeader("Completed History", ColorCompleted) }
                    items(completed) { s ->
                        CompletedCard(s, repository)
                    }
                }

                if (upcoming.isEmpty() && missed.isEmpty() && completed.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No schedule records found.", color = TextLabel)
                        }
                    }
                }
            }
        }
    }
}

// --- Helper Composable for Section Headers ---
@Composable
fun SectionHeader(title: String, color: Color) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = color,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

/* ---------------------------------------------------------------
   Premium Schedule Card (Upcoming / Missed)
----------------------------------------------------------------*/
@Composable
fun ScheduleCard(
    schedule: Schedule,
    repository: AppRepository,
    statusColor: Color,
    onMarkCompleted: () -> Unit,
    onReschedule: () -> Unit
) {
    var vaccineName by remember { mutableStateOf("Loading...") }

    LaunchedEffect(schedule.vaccineId) {
        vaccineName = repository.getVaccineById(schedule.vaccineId)?.vaccineName ?: "Vaccine #${schedule.vaccineId}"
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                // Status Icon Box
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(statusColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (statusColor == ColorMissed) Icons.Default.Warning else Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Text Content
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = vaccineName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextHead
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Due: ${schedule.dueDate}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = TextLabel
                    )
                    Text(
                        text = schedule.status.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = SurfaceBg)
            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reschedule (Text Button)
                TextButton(onClick = onReschedule) {
                    Icon(Icons.Default.EditCalendar, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reschedule", color = TextLabel)
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Done (Filled Button)
                Button(
                    onClick = onMarkCompleted,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Mark Done", fontSize = 12.sp)
                }
            }
        }
    }
}

/* ---------------------------------------------------------------
   Completed Card (Simpler design)
----------------------------------------------------------------*/
@Composable
fun CompletedCard(schedule: Schedule, repository: AppRepository) {
    var vaccineName by remember { mutableStateOf("Loading...") }

    LaunchedEffect(schedule.vaccineId) {
        vaccineName = repository.getVaccineById(schedule.vaccineId)?.vaccineName ?: "Vaccine"
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.6f), // Slightly transparent to show it's history
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Completed",
                tint = ColorCompleted,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = vaccineName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = TextHead.copy(alpha = 0.8f)
                )
                Text(
                    text = "Administered on ${schedule.dueDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextLabel
                )
            }
        }
    }
}