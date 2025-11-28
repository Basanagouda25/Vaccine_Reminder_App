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
import com.basu.vaccineremainder.features.auth.ProviderAuthViewModel

// --- Colors matching the design ---
private val PrimaryIndigo = Color(0xFF4F46E5)
private val TextHead = Color(0xFF0F172A)
private val TextLabel = Color(0xFF334155)
private val TextPlaceholder = Color(0xFF94A3B8)
private val InputBorder = Color(0xFFE2E8F0)
private val InputBg = Color(0xFFF8FAFC)

@Composable
fun ProviderRegistrationScreen(
    viewModel: ProviderAuthViewModel,
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit,
    onLoginClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var clinicName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    val registrationSuccessful by viewModel.registerSuccess.collectAsState()

    LaunchedEffect(registrationSuccessful) {
        if (registrationSuccessful) {
            onRegisterSuccess()
            viewModel.onRegistrationComplete()
        }
    }

    // Use a basic Box for the screen background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Use a single Column that is scrollable for all content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp), // Main horizontal padding
            horizontalAlignment = Alignment.Start
        ) {

            Spacer(modifier = Modifier.height(32.dp))

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

            // Header
            Text(
                text = "Create Provider Account",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                ),
                color = TextHead
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Fill in the details to get started.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            CustomTextField(
                label = "Full Name",
                value = name,
                onValueChange = { name = it },
                placeholder = "your name"
            )

            Spacer(Modifier.height(16.dp))

            CustomTextField(
                label = "Clinic Name",
                value = clinicName,
                onValueChange = { clinicName = it },
                placeholder = "City Health Clinic"
            )

            Spacer(Modifier.height(16.dp))

            CustomTextField(
                label = "Phone Number",
                value = phone,
                onValueChange = { phone = it },
                placeholder = "+1 234 567 890",
                keyboardType = KeyboardType.Phone
            )

            Spacer(Modifier.height(16.dp))

            CustomTextField(
                label = "Email Address",
                value = email,
                onValueChange = { email = it },
                placeholder = "you@example.com",
                keyboardType = KeyboardType.Email
            )

            Spacer(Modifier.height(16.dp))

            CustomTextField(
                label = "Password",
                value = password,
                onValueChange = { password = it },
                placeholder = "••••••••",
                isPassword = true
            )

            Spacer(Modifier.height(24.dp))

            if (errorMsg.isNotEmpty()) {
                Text(
                    text = errorMsg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(16.dp))
            }

            // Register Button
            Button(
                onClick = {
                    if (name.isBlank() || email.isBlank() || password.isBlank() || clinicName.isBlank() || phone.isBlank()) {
                        errorMsg = "Please fill all fields"
                        return@Button
                    }
                    viewModel.registerProvider(
                        name = name,
                        email = email,
                        pass = password,
                        clinic = clinicName,
                        phone = phone
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryIndigo
                )
            ) {
                Text(
                    text = "Register",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("Already have an account? ")
                        withStyle(SpanStyle(color = PrimaryIndigo, fontWeight = FontWeight.Bold)) {
                            append("Log In")
                        }
                    },
                    modifier = Modifier.clickable { onLoginClick() },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Reusable Text Field Component is unchanged
@Composable
fun CustomTextField(
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
