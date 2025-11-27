package com.basu.vaccineremainder.features.provider

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.basu.vaccineremainder.data.repository.AppRepository
import com.basu.vaccineremainder.features.auth.ProviderAuthViewModel
import kotlinx.coroutines.launch

@Composable
fun ProviderLoginScreen(
    viewModel: ProviderAuthViewModel,
    repository: AppRepository,
    onLoginSuccess: (Int) -> Unit,
    onNavigateToRegister: () -> Unit,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Button(onClick = onBack) {
            Text("⬅ Back")
        }

        Spacer(Modifier.height(20.dp))

        Text("Provider Login", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(20.dp))

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

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                scope.launch {
                    val provider = repository.getProviderByEmail(email)
                    if (provider == null || provider.password != password) {
                        errorMessage = "Invalid email or password"
                    } else {
                        onLoginSuccess(provider.providerId)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(Modifier.height(10.dp))

        TextButton(onClick = onNavigateToRegister) {
            Text("New Provider? Register Here")
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text(errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}
