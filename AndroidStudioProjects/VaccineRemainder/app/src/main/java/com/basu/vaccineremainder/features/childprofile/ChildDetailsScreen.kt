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
    onBack: () -> Unit
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
            .padding(24.dp)
    ) {
        Button(onClick = { onBack() }) {
            Text("⬅ Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (child == null) {
            Text("Loading child details...")
        } else {
            Text(
                text = "Child Details",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Name: ${child!!.name}", style = MaterialTheme.typography.titleMedium)
            Text("DOB: ${child!!.dateOfBirth}")
            Text("Gender: ${child!!.gender}")

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    // TODO: Navigate to vaccination schedule
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Vaccination Schedule")
            }
        }
    }
}
