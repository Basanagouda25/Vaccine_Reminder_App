package com.basu.vaccineremainder.features.provider

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.basu.vaccineremainder.data.model.Child
import com.basu.vaccineremainder.features.auth.ProviderAuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewPatientsScreen(
    viewModel: ProviderAuthViewModel,
    onBack: () -> Unit
) {
    // ✅ USE children from ViewModel
    val children by viewModel.children.collectAsState()
    val providerState by viewModel.providerState.collectAsState()

    // Load children once provider is available
    LaunchedEffect(providerState) {
        if (providerState != null) {
            // This will start observing the Firestore "children" collection
            viewModel.loadProviderData()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registered Patients") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No patients have been registered yet.")
            }
        } else {
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
                text = "Parent Email: ${child.parentEmail}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
