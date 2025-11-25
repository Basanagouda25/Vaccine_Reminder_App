package com.basu.vaccineremainder.features.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.basu.vaccineremainder.data.model.Vaccine
import com.basu.vaccineremainder.data.repository.AppRepository
import kotlinx.coroutines.launch

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Button(
            onClick = { onBack() },
            //modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }


        Spacer(modifier = Modifier.height(20.dp))

        if (vaccine == null) {
            Text("Loading vaccine details...")
        } else {
            Text(
                text = vaccine!!.vaccineName,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text("Recommended Age: ${vaccine!!.recommendedAgeMonths} months")

            Spacer(modifier = Modifier.height(10.dp))
            Text("Description:", style = MaterialTheme.typography.titleMedium)
            Text(vaccine!!.description)

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    // TODO: Navigate to schedule screen
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Schedule / Set Reminder")
            }
        }
    }
}

