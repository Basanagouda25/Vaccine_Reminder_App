package com.basu.vaccineremainder.features.provider

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*import androidx.compose.foundation.lazy.LazyColumn
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
import com.basu.vaccineremainder.data.model.Child
import com.basu.vaccineremainder.features.auth.ProviderAuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderSendNotificationScreen(
    viewModel: ProviderAuthViewModel,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    // FIX 1: This state now correctly holds the String-based document ID.
    var selectedChildDocId by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val children by viewModel.children.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Send Vaccination Alert") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
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

            LazyColumn(modifier = Modifier.weight(1f)) {
                if (children.isEmpty()) {
                    item { Text("No patients available to notify.") }
                } else {
                    items(children) { child ->
                        // FIX 2: Pass the documentId and update selectedChildDocId
                        ChildSelectionRow(
                            child = child,
                            isSelected = selectedChildDocId == child.documentId,
                            onSelected = { docId -> selectedChildDocId = docId }
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                // FIX 3: Check against the correct state variable (selectedChildDocId)
                enabled = selectedChildDocId != null && title.isNotBlank() && message.isNotBlank() && !isLoading,
                onClick = {
                    isLoading = true
                    // Use the correct String ID
                    selectedChildDocId?.let { docId ->
                        scope.launch {
                            // FIX 4: This call now works because docId is a String
                            val success = withContext(Dispatchers.IO) {
                                viewModel.sendNotificationToChild(docId, title, message)
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
    // FIX 5: This function signature is now correct and consistent
    onSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            // Pass the String documentId on click
            .clickable { onSelected(child.documentId) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            // You can also set this to null, but having it here is also correct
            onClick = { onSelected(child.documentId) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(child.name, style = MaterialTheme.typography.bodyLarge)
    }
}
