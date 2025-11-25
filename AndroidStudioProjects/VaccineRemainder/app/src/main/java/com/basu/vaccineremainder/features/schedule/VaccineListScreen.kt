package com.basu.vaccineremainder.features.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.basu.vaccineremainder.data.model.Vaccine
import com.basu.vaccineremainder.data.repository.AppRepository
import kotlinx.coroutines.launch

@Composable
fun VaccineListScreen(
    repository: AppRepository,
    onVaccineSelected: (Int) -> Unit
) {
    var vaccineList by remember { mutableStateOf<List<Vaccine>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            vaccineList = repository.getAllVaccines()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Vaccination List",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(vaccineList) { vaccine ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { onVaccineSelected(vaccine.vaccineId) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(vaccine.vaccineName, style = MaterialTheme.typography.titleMedium)
                        Text("Recommended Age: ${vaccine.recommendedAgeMonths} months")
                    }
                }
            }
        }
    }
}
