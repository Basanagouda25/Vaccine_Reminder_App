package com.basu.vaccineremainder.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.basu.vaccineremainder.data.model.User
import kotlinx.coroutines.flow.collectLatest

// --- Uniform Color Palette ---
private val SlateDark = Color(0xFF556080)    // Premium Header
private val PrimaryIndigo = Color(0xFF4F46E5)
private val TextHead = Color(0xFF0F172A)
private val TextLabel = Color(0xFF334155)
private val TextPlaceholder = Color(0xFF94A3B8)
private val InputBorder = Color(0xFFE2E8F0)
private val InputBg = Color(0xFFF8FAFC)

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginResult: (User) -> Unit,
    onNavigateToRegister: () -> Unit,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    // Listen for login results from ViewModel
    LaunchedEffect(Unit) {
        viewModel.loginResult.collectLatest {
            loading = false
            it?.let { user ->
                onLoginResult(user)
            } ?: run {
                if (email.isNotBlank()) error = "Invalid email or password"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // HEADER SECTION
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBack() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = Color.White
            )

            Text(
                text = "Secure login with your email and password.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // FORM AREA
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Sign In",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextHead
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Email Field
                AuthTextField(
                    label = "Email Address",
                    value = email,
                    onChange = { email = it },
                    type = KeyboardType.Email,
                    placeholder = "you@example.com"
                )

                Spacer(Modifier.height(20.dp))

                // Password Field
                AuthTextField(
                    label = "Password",
                    value = password,
                    onChange = { password = it },
                    type = KeyboardType.Password,
                    isPassword = true,
                    placeholder = "••••••••"
                )

                Spacer(Modifier.height(32.dp))

                if (error.isNotEmpty()) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Login Button
                Button(
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            loading = true
                            error = ""
                            viewModel.loginUser(email, password)
                        } else {
                            error = "Please fill in all fields"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                    enabled = !loading
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Login",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Footer Link
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append("Don't have an account? ")
                            withStyle(SpanStyle(color = PrimaryIndigo, fontWeight = FontWeight.Bold)) {
                                append("Register")
                            }
                        },
                        modifier = Modifier.clickable { onNavigateToRegister() },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun AuthTextField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    type: KeyboardType,
    isPassword: Boolean = false,
    placeholder: String
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = TextLabel,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            placeholder = { Text(text = placeholder, color = TextPlaceholder) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryIndigo,
                unfocusedBorderColor = InputBorder,
                focusedContainerColor = InputBg,
                unfocusedContainerColor = InputBg,
                cursorColor = PrimaryIndigo,
                focusedTextColor = TextHead,
                unfocusedTextColor = TextHead
            ),
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            keyboardOptions = KeyboardOptions(keyboardType = type),
            singleLine = true,
            trailingIcon = {
                if (isPassword) {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null, tint = TextLabel)
                    }
                }
            }
        )
    }
}