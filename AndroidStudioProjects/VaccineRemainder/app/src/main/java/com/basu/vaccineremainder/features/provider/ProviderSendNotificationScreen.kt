package com.basu.vaccineremainder.features.provider

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.basu.vaccineremainder.data.model.AppNotification
import com.basu.vaccineremainder.data.model.Child
import com.basu.vaccineremainder.features.auth.ProviderAuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderSendNotificationScreen(
    // --- FIX 1: Use the ViewModel, not the Repository ---
    viewModel: ProviderAuthViewModel,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var selectedChildId by remember { mutableStateOf<Int?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // --- FIX 2: Get the list from the shared ViewModel ---
    val children by viewModel.childrenList.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Send Vaccination Alert") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // --- Form Fields ---
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Notification Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Notification Message") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(Modifier.height(20.dp))

            Text("Select Child to Notify:", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))

            // --- Children List ---
            LazyColumn(
                // Use weight to make the list take available space
                modifier = Modifier.weight(1f)
            ) {
                if (children.isEmpty()) {
                    item {
                        Text("No patients available to notify.")
                    }
                } else {
                    items(children) { child ->
                        ChildSelectionRow(
                            child = child,
                            isSelected = selectedChildId == child.childId,
                            onSelected = { selectedChildId = it }
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // --- Send Button ---
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = selectedChildId != null && title.isNotBlank() && message.isNotBlank() && !isLoading,
                onClick = {
                    isLoading = true
                    selectedChildId?.let { childId ->
                        // The logic here is already good, just add background thread handling
                        scope.launch {
                            val success = withContext(Dispatchers.IO) {
                                viewModel.sendNotificationToChild(childId, title, message)
                            }
                            if (success) {
                                Toast.makeText(context, "Notification Sent!", Toast.LENGTH_SHORT).show()
                                onBack()
                            } else {
                                Toast.makeText(context, "Failed to send notification.", Toast.LENGTH_SHORT).show()
                            }
                            isLoading = false
                        }
                    }
                }
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Send Notification", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ChildSelectionRow(
    child: Child,
    isSelected: Boolean,
    onSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected(child.childId) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onSelected(child.childId) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(child.name, style = MaterialTheme.typography.bodyLarge)
    }
}
