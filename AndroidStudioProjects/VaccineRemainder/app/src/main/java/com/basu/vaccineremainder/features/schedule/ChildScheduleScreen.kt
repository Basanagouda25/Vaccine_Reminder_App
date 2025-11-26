package com.basu.vaccineremainder.features.schedule

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basu.vaccineremainder.data.model.Schedule
import com.basu.vaccineremainder.data.repository.AppRepository
import androidx.compose.foundation.lazy.items


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChildScheduleScreen(
    repository: AppRepository,
    childId: Int,
    onBack: () -> Unit
) {
    val viewModel: ChildScheduleViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = ChildScheduleViewModelFactory(repository)
    )

    val upcoming by viewModel.upcoming.collectAsState()
    val completed by viewModel.completed.collectAsState()
    val missed by viewModel.missed.collectAsState()

    var selectedSchedule by remember { mutableStateOf<Schedule?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    // --- DATE PICKER DIALOG ---
    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { newDate ->
                selectedSchedule?.let {
                    viewModel.reschedule(it.scheduleId, newDate)
                }
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    // Load schedules
    LaunchedEffect(childId) {
        viewModel.detectAndMarkMissed(childId)
        viewModel.loadSchedules(childId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Button(onClick = onBack) {
            Text("⬅ Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Vaccination Schedule",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ⭐ SCROLLABLE CONTENT
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {

            // UPCOMING
            if (upcoming.isNotEmpty()) {
                item {
                    Text(
                        "Upcoming",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF0277BD),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(upcoming) { s ->
                    ScheduleCard(
                        schedule = s,
                        repository = repository,
                        onMarkCompleted = {
                            viewModel.markScheduleCompleted(s.scheduleId)
                        },
                        onReschedule = {
                            selectedSchedule = s
                            showDatePicker = true
                        }
                    )
                }
            }

            // MISSED
            if (missed.isNotEmpty()) {
                item {
                    Text(
                        "Missed",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFFD32F2F),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(missed) { s ->
                    ScheduleCard(
                        schedule = s,
                        repository = repository,
                        onMarkCompleted = {
                            viewModel.markScheduleCompleted(s.scheduleId)
                        },
                        onReschedule = {
                            selectedSchedule = s
                            showDatePicker = true
                        }
                    )
                }
            }

            // COMPLETED
            if (completed.isNotEmpty()) {
                item {
                    Text(
                        "Completed",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(completed) { s ->
                    CompletedCard(s)
                }
            }
        }
    }
}



/* ---------------------------------------------------------------
   Schedule Card
----------------------------------------------------------------*/
@Composable
fun ScheduleCard(
    schedule: Schedule,
    repository: AppRepository,
    onMarkCompleted: (Schedule) -> Unit,
    onReschedule: (Schedule) -> Unit
) {
    var vaccineName by remember { mutableStateOf("Loading...") }
    LaunchedEffect(schedule.vaccineId) {
        vaccineName = repository.getVaccineById(schedule.vaccineId)?.vaccineName ?: "Vaccine"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(vaccineName, style = MaterialTheme.typography.titleMedium)
            Text("Due: ${schedule.dueDate}")
            Text("Status: ${schedule.status}")

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { onMarkCompleted(schedule) }) { Text("Done") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onReschedule(schedule) }) { Text("Reschedule") }
            }
        }
    }
}



/* ---------------------------------------------------------------
   Completed Card
----------------------------------------------------------------*/
@Composable
fun CompletedCard(schedule: Schedule) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Vaccine ID: ${schedule.vaccineId}")
            Text("Date: ${schedule.dueDate}")
            Text("Completed", color = Color(0xFF2E7D32))
        }
    }
}

