package com.basu.vaccineremainder.features.provider

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.basu.vaccineremainder.data.repository.AppRepository
import com.basu.vaccineremainder.features.auth.ProviderAuthViewModel
import kotlinx.coroutines.launch

// --- Consistent Colors from Registration Screen ---
private val PrimaryIndigo = Color(0xFF4F46E5)
private val TextHead = Color(0xFF0F172A)
private val TextLabel = Color(0xFF334155)
private val TextPlaceholder = Color(0xFF94A3B8)
private val InputBorder = Color(0xFFE2E8F0)
private val InputBg = Color(0xFFF8FAFC)

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
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // --- FIX: Change the parent layout to center the content ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp), // Apply horizontal padding here
        contentAlignment = Alignment.Center // Center the content vertically and horizontally
    ) {
        // This Column will now be centered inside the Box, but can still scroll if needed
        Column(
            modifier = Modifier
                .fillMaxWidth() // Take full width but height is determined by content
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            // Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onBack() }
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Back",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Header ---
            Text(
                text = "Welcome Back, Provider",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                ),
                color = TextHead
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Log in to your account to continue.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- Form Fields ---
            ProviderLoginTextField(
                label = "Email Address",
                value = email,
                onValueChange = { email = it },
                placeholder = "you@example.com",
                keyboardType = KeyboardType.Email
            )

            Spacer(Modifier.height(16.dp))

            ProviderLoginTextField(
                label = "Password",
                value = password,
                onValueChange = { password = it },
                placeholder = "••••••••",
                isPassword = true
            )

            Spacer(Modifier.height(24.dp))

            // Display error message
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // --- Login Button ---
            Button(
                onClick = {
                    isLoading = true
                    errorMessage = ""
                    scope.launch {
                        val provider = repository.getProviderByEmail(email)
                        if (provider == null || provider.password != password) {
                            errorMessage = "Invalid email or password"
                        } else {
                            onLoginSuccess(provider.providerId)
                        }
                        isLoading = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryIndigo
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Footer Link to Register ---
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("New Provider? ")
                        withStyle(SpanStyle(color = PrimaryIndigo, fontWeight = FontWeight.Bold)) {
                            append("Register Here")
                        }
                    },
                    modifier = Modifier.clickable { onNavigateToRegister() },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            // Add a final spacer for padding at the bottom when scrolled
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// The renamed function remains the same
@Composable
private fun ProviderLoginTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = TextLabel,
            modifier = Modifier.padding(start = 2.dp, bottom = 6.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            placeholder = {
                Text(text = placeholder, color = TextPlaceholder)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryIndigo,
                unfocusedBorderColor = InputBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = InputBg,
                cursorColor = PrimaryIndigo,
                focusedTextColor = TextHead,
                unfocusedTextColor = TextHead
            ),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true
        )
    }
}
