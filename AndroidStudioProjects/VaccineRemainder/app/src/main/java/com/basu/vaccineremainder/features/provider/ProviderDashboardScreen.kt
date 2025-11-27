package com.basu.vaccineremainder.features.provider

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.basu.vaccineremainder.features.auth.ProviderAuthViewModel

@Composable
fun ProviderDashboardScreen(
    viewModel: ProviderAuthViewModel,
    onViewChildrenClick: () -> Unit,
    onSendNotificationClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Provider Dashboard", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onViewChildrenClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View Children")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onSendNotificationClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send Notification")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}
