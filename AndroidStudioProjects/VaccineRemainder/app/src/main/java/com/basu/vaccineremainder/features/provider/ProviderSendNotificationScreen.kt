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

    // 🔁 store the selected Child itself, not just docId
    var selectedChild by remember { mutableStateOf<Child?>(null) }

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
                        ChildSelectionRow(
                            child = child,
                            isSelected = selectedChild?.childId == child.childId,
                            onSelected = { selectedChild = child }   // ⬅️ store full Child
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = selectedChild != null &&
                        title.isNotBlank() &&
                        message.isNotBlank() &&
                        !isLoading,
                onClick = {
                    isLoading = true
                    val child = selectedChild
                    if (child != null) {
                        scope.launch {
                            val success = withContext(Dispatchers.IO) {
                                // ⬅️ Now we pass the Child object
                                viewModel.sendNotificationToChild(child, title, message)
                            }
                            if (success) {
                                Toast.makeText(context, "Notification Sent!", Toast.LENGTH_SHORT).show()
                                onBack()
                            } else {
                                Toast.makeText(context, "Failed to send notification.", Toast.LENGTH_SHORT).show()
                            }
                            isLoading = false
                        }
                    } else {
                        isLoading = false
                    }
                }
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
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
    // 🔁 onSelected now passes Child instead of String
    onSelected: (Child) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected(child) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onSelected(child) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(child.name, style = MaterialTheme.typography.bodyLarge)
    }
}
