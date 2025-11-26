package com.basu.vaccineremainder.features.childprofile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.basu.vaccineremainder.data.model.Child
import com.basu.vaccineremainder.data.repository.AppRepository
import kotlinx.coroutines.launch

@Composable
fun ChildDetailsScreen(
    repository: AppRepository,
    childId: Int,
    onBack: () -> Unit,
    onViewSchedule: (Int) -> Unit
) {
    var child by remember { mutableStateOf<Child?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            child = repository.getChildById(childId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp) // ⭐ Proper padding
    ) {
        Button(onClick = { onBack() }) {
            Text("⬅ Back")
        }

        Spacer(modifier = Modifier.height(24.dp)) // ⭐ More spacing

        if (child == null) {
            Text("Loading child details...")
        } else {
            Text(
                text = "Child Details",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Name: ${child!!.name}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text("DOB: ${child!!.dateOfBirth}")
            Spacer(modifier = Modifier.height(8.dp))

            Text("Gender: ${child!!.gender}")

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = { onViewSchedule(childId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Text("View Vaccination Schedule")
            }
        }
    }
}
