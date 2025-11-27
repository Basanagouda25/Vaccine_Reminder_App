package com.basu.vaccineremainder.features.provider

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.basu.vaccineremainder.data.model.AppNotification
import com.basu.vaccineremainder.data.repository.AppRepository
import kotlinx.coroutines.launch

@Composable
fun ProviderSendNotificationScreen(
    repository: AppRepository,
    onBack: () -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }
    var selectedChildId by rememberSaveable { mutableStateOf<Int?>(null) }

    val scope = rememberCoroutineScope()

    // --- THE FIX IS HERE ---
    // Before: repository.getChildrenByParentId(1)
    // After: repository.getAllChildren()
    // This now fetches ALL children from the database, which is correct for the provider.
    val children by repository.getAllChildren()
        .collectAsState(initial = emptyList())
    // ----------------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Button(onClick = onBack) {
            Text("⬅ Back")
        }

        Spacer(Modifier.height(16.dp))

        Text("Send Notification", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Message") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Text("Select Child:", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.height(200.dp)
        ) {
            items(children) { child ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { selectedChildId = child.childId },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedChildId == child.childId,
                        onClick = { selectedChildId = child.childId }
                    )
                    Text(child.name)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        //... inside the onClick for the "Send Notification" button
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Ensure a child is selected
                selectedChildId?.let { childId ->
                    scope.launch {
                        // 1. Find the child in the database to get their parentId
                        val child = repository.getChildById(childId)
                        child?.let {
                            // 2. Create the notification WITH the parentId
                            val notification = AppNotification(
                                title = title,
                                message = message,
                                timestamp = System.currentTimeMillis(),
                                parentId = it.parentId
                            )


                            repository.insertNotification(notification)

                            onBack()
                        }
                    }
                }
            }
        ) {
            Text("Send Notification")
        }

    }
}
