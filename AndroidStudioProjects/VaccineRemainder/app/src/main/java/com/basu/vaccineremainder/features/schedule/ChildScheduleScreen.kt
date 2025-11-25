package com.basu.vaccineremainder.features.schedule

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.basu.vaccineremainder.data.model.Schedule
import com.basu.vaccineremainder.data.repository.AppRepository
import kotlinx.coroutines.launch

/**
 * Card-based schedule screen (Option B).
 *
 * NOTE: This screen uses ChildScheduleViewModel, but for simplicity this composable
 * constructs a local ViewModel instance via remember (not ViewModelProvider).
 * If you prefer lifecycle-aware ViewModel creation, instantiate via ViewModelProvider in NavGraph.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChildScheduleScreen(
    repository: AppRepository,
    childId: Int,
    onBack: () -> Unit,
    onRequestReschedule: (Schedule) -> Unit = {}
) {
    // Create a lightweight ViewModel instance tied to composition (not lifecycle-bound).
    val vm = remember { ChildScheduleViewModel(repository) }
    val scope = rememberCoroutineScope()

    val upcoming by vm.upcoming.collectAsState()
    val completed by vm.completed.collectAsState()
    val missed by vm.missed.collectAsState()

    // initial load and missed detection
    LaunchedEffect(childId) {
        vm.detectAndMarkMissed(childId)
        vm.loadSchedules(childId)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Vaccination Schedule", style = MaterialTheme.typography.headlineMedium)
            Button(onClick = { onBack() }) { Text("Back") }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // UPCOMING
        if (upcoming.isNotEmpty()) {
            Text("Upcoming", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(upcoming) { schedule ->
                    ScheduleCard(
                        schedule = schedule,
                        repository = repository,
                        onMarkCompleted = { sched ->
                            scope.launch { vm.markScheduleCompleted(sched.scheduleId) }
                        },
                        onReschedule = { sched ->
                            onRequestReschedule(sched)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        } else {
            Text("No upcoming vaccines", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // MISSED
        if (missed.isNotEmpty()) {
            Text("Missed", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(missed) { schedule ->
                    ScheduleCard(
                        schedule = schedule,
                        repository = repository,
                        onMarkCompleted = { sched ->
                            scope.launch { vm.markScheduleCompleted(sched.scheduleId) }
                        },
                        onReschedule = { sched ->
                            onRequestReschedule(sched)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // COMPLETED
        if (completed.isNotEmpty()) {
            Text("Completed", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(completed) { schedule ->
                    CompletedCard(schedule = schedule)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun ScheduleCard(
    schedule: Schedule,
    repository: AppRepository,
    onMarkCompleted: (Schedule) -> Unit,
    onReschedule: (Schedule) -> Unit
) {
    // Small helper to show vaccine name: fetch vaccine synchronously via remember block
    var vaccineName by remember { mutableStateOf("Loading...") }
    LaunchedEffect(schedule.vaccineId) {
        val v = repository.getVaccineById(schedule.vaccineId)
        vaccineName = v?.vaccineName ?: "Vaccine"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(vaccineName, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Due: ${schedule.dueDate}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(6.dp))
                val status = schedule.status
                val statusColor = when (status.lowercase()) {
                    "completed" -> Color(0xFF2E7D32) // green
                    "missed" -> Color(0xFFD32F2F)     // red
                    else -> Color(0xFF0277BD)         // blue for upcoming/pending
                }
                Text(status.replaceFirstChar { it.uppercase() }, color = statusColor)
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                if (!schedule.status.equals("Completed", ignoreCase = true)) {
                    Button(onClick = { onMarkCompleted(schedule) }) {
                        Text("Mark Completed")
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(onClick = { onReschedule(schedule) }) {
                        Text("Reschedule")
                    }
                } else {
                    Text("Done")
                }
            }
        }
    }
}

@Composable
private fun CompletedCard(schedule: Schedule) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Vaccine ID: ${schedule.vaccineId}", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Date: ${schedule.dueDate}", style = MaterialTheme.typography.bodyMedium)
            }
            Text("Completed", color = Color(0xFF2E7D32))
        }
    }
}
