package com.basu.vaccineremainder.features.provider

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.basu.vaccineremainder.data.model.Child
import com.basu.vaccineremainder.features.auth.ProviderAuthViewModel
import androidx.compose.runtime.LaunchedEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewPatientsScreen(
    viewModel: ProviderAuthViewModel,
    onBack: () -> Unit
) {
    // Get the list from the shared ViewModel
    val children by viewModel.childrenList.collectAsState()
    val providerState by viewModel.providerState.collectAsState()

    // In ViewPatientsScreen.kt

    LaunchedEffect(key1 = providerState) {
        // This check ensures we only load data once we have a logged-in provider
        if (providerState != null) {
            // Call the correct function that loads children for the current provider
            viewModel.loadProviderData()
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registered Patients") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        if (children.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No patients have been registered yet.")
            }
        } else {
            // Use a LazyColumn to display a list of items efficiently
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(children) { child ->
                    PatientInfoCard(child)
                }
            }
        }
    }
}

@Composable
fun PatientInfoCard(child: Child) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = child.name,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Parent Email: ${child.parentEmail}", // Assuming parentEmail is a field
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
