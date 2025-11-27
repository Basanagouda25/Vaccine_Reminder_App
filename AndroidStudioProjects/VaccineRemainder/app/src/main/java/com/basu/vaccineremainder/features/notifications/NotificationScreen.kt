package com.basu.vaccineremainder.features.notifications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
// DO NOT import NavController here
import com.basu.vaccineremainder.data.repository.AppRepository
import com.basu.vaccineremainder.util.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    repository: AppRepository,
    onBack : () -> Unit // Only accept the onBack lambda
) {
    val context = LocalContext.current
    val parentId = SessionManager.getCurrentUserId(context)

    val notifications by repository.getNotificationsForParent(parentId)
        .collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Notifications") },
                // Add the navigation icon that triggers the onBack function
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (notifications.isEmpty()) {
                item {
                    Text("You have no notifications.")
                }
            } else {
                items(notifications) { notification ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(notification.title, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(4.dp))
                            Text(notification.message, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
