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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basu.vaccineremainder.data.model.AppNotification
import com.basu.vaccineremainder.data.repository.AppRepository
import com.basu.vaccineremainder.util.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    repository: AppRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val parentEmail = remember {
        SessionManager.getParentEmail(context) ?: ""
    }

    LaunchedEffect(Unit) {
        println("🔍 NotificationScreen STARTED, parentEmail='$parentEmail'")
    }

    // 🔹 Get ViewModel using factory
    val viewModel: NotificationViewModel = viewModel(
        factory = NotificationViewModelFactory(repository, parentEmail)
    )

    // 🔹 Collect StateFlow as Compose state
    val notifications by viewModel.notifications.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Notifications") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Logged in as: ${if (parentEmail.isBlank()) "UNKNOWN" else parentEmail}",
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (notifications.isEmpty()) {
                    item { Text("You have no notifications.") }
                } else {
                    items(notifications) { notification ->
                        NotificationCard(notification)
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(notification: AppNotification) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                notification.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                notification.message,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
