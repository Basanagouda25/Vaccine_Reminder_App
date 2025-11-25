package com.basu.vaccineremainder.features.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(
    onAddChildClick: () -> Unit,
    onChildListClick: () -> Unit,
    onVaccineScheduleClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Dashboard", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { onAddChildClick() }, modifier = Modifier.fillMaxWidth()) {
            Text("Add Child")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = { onChildListClick() }, modifier = Modifier.fillMaxWidth()) {
            Text("View Child List")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = { onVaccineScheduleClick() }, modifier = Modifier.fillMaxWidth()) {
            Text("Vaccination Schedule")
        }
    }
}
