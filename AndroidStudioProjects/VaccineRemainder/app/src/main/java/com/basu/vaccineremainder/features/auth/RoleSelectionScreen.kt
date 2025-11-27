package com.basu.vaccineremainder.features.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RoleSelectionScreen(
    onUserClick: () -> Unit,
    onProviderClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Select Role", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = onUserClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("I am a Parent/User")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onProviderClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("I am a Provider")
        }
    }
}
