package com.basu.vaccineremainder.features.provider

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.basu.vaccineremainder.features.auth.ProviderAuthViewModel

@Composable
fun ProviderRegistrationScreen(
    viewModel: ProviderAuthViewModel,
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit,
    // The 'repository' parameter is no longer needed here
    // as the ViewModel will handle all repository interactions.
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var clinicName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    var errorMsg by remember { mutableStateOf("") }

    // --- FIX 1: Observe the registerSuccess state from the ViewModel ---
    // When the ViewModel sets registerSuccess to true, this will trigger the navigation.
    val registrationSuccessful by viewModel.registerSuccess.collectAsState()
    LaunchedEffect(registrationSuccessful) {
        if (registrationSuccessful) {
            onRegisterSuccess()
            viewModel.onRegistrationComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Button(onClick = onBack) { Text("⬅ Back") }

        Spacer(Modifier.height(20.dp))

        Text("Provider Registration", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Provider Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = clinicName,
            onValueChange = { clinicName = it },
            label = { Text("Clinic Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        if (errorMsg.isNotEmpty()) {
            Text(errorMsg, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(10.dp))
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (name.isBlank() || email.isBlank() || password.isBlank() || clinicName.isBlank() || phone.isBlank()) {
                    errorMsg = "Please fill all fields"
                    return@Button
                }

                // --- FIX 2: Call the ViewModel function instead of the repository ---
                // This correctly runs the database operation on a background thread.
                viewModel.registerProvider(
                    name = name,
                    email = email,
                    pass = password,
                    clinic = clinicName,
                    phone = phone
                )
            }
        ) {
            Text("Register Provider")
        }
    }
}
