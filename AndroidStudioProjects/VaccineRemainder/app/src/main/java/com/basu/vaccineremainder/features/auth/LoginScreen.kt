package com.basu.vaccineremainder.features.auth

import android.app.Activity
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
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import kotlinx.coroutines.flow.collectLatest

enum class LoginMode { EMAIL, PHONE }

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
    val context = LocalContext.current

    var loginMode by remember { mutableStateOf(LoginMode.EMAIL) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }

    var error by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    // --- Unified Login Logic ---
    // Both email and phone success paths will call onLoginResult.
    // AppNavGraph will handle the session and navigation.

    LaunchedEffect(Unit) {
        viewModel.loginResult.collectLatest {
            loading = false
            it?.let { user ->
                onLoginResult(user) // Pass user up to NavGraph
            } ?: run { if(email.isNotBlank()) error = "Invalid email or password" }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.otpSent.collectLatest {
            loading = false
            otpSent = it
            if (!it && phone.isNotBlank()) error = "Failed to send OTP."
        }
    }
    LaunchedEffect(Unit) {
        viewModel.loginResult.collectLatest { user ->
            user?.let { onLoginResult(it) }
        }
    }



    // --- Root Container (Dark Background) ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark)
    ) {
        // This is the Header section
        Spacer(modifier = Modifier.height(8.dp))
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
                text = "Secure login to manage your family.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- Sliding Surface (Form Area) ---
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

                // --- Tab Row (Email / Phone) ---
                TabRow(
                    selectedTabIndex = loginMode.ordinal,
                    containerColor = Color.White,
                    contentColor = PrimaryIndigo,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[loginMode.ordinal]),
                            color = PrimaryIndigo
                        )
                    },
                    divider = { HorizontalDivider(color = InputBorder) }
                ) {
                    Tab(
                        selected = loginMode == LoginMode.EMAIL,
                        onClick = { loginMode = LoginMode.EMAIL },
                        text = { Text("Email", fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = loginMode == LoginMode.PHONE,
                        onClick = { loginMode = LoginMode.PHONE },
                        text = { Text("Phone OTP", fontWeight = FontWeight.SemiBold) }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- Input Fields ---
                if (loginMode == LoginMode.EMAIL) {
                    AuthTextField(
                        label = "Email Address",
                        value = email,
                        onChange = { email = it },
                        type = KeyboardType.Email,
                        placeholder = "you@example.com"
                    )
                    Spacer(Modifier.height(20.dp))
                    AuthTextField(
                        label = "Password",
                        value = password,
                        onChange = { password = it },
                        type = KeyboardType.Password,
                        isPassword = true,
                        placeholder = "••••••••"
                    )
                } else {
                    AuthTextField(
                        label = "Phone Number",
                        value = phone,
                        onChange = { phone = it },
                        type = KeyboardType.Phone,
                        placeholder = "+91 123 456 7890"
                    )

                    if (otpSent) {
                        Spacer(Modifier.height(20.dp))
                        AuthTextField(
                            label = "Enter OTP",
                            value = otp,
                            onChange = { otp = it },
                            type = KeyboardType.Number,
                            placeholder = "123456"
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                // Error Message
                if (error.isNotEmpty()) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // --- Action Button ---
                Button(
                    onClick = {
                        loading = true
                        error = ""
                        if (loginMode == LoginMode.EMAIL) {
                            viewModel.loginUser(email, password)
                        } else {
                            if (!otpSent) {
                                viewModel.sendOtp(phone, context as Activity)
                            } else {
                                viewModel.verifyOtp(otp)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryIndigo
                    ),
                    enabled = !loading
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        val btnText = if (loginMode == LoginMode.EMAIL) "Login" else if (!otpSent) "Send OTP" else "Verify & Login"
                        Text(
                            text = btnText,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- Footer Link ---
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
// --- Reusable Styled TextField with Password Toggle ---
@Composable
private fun AuthTextField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    type: KeyboardType,
    isPassword: Boolean = false,
    placeholder: String
) {
    // State to track if the password should be visible or masked
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
            placeholder = {
                Text(text = placeholder, color = TextPlaceholder)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryIndigo,
                unfocusedBorderColor = InputBorder,
                focusedContainerColor = InputBg,
                unfocusedContainerColor = InputBg,
                cursorColor = PrimaryIndigo,
                focusedTextColor = TextHead,
                unfocusedTextColor = TextHead
            ),
            // Logic to switch between Password and Normal text transformation
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            keyboardOptions = KeyboardOptions(keyboardType = type),
            singleLine = true,
            // Add the Eye Icon toggle here
            trailingIcon = {
                if (isPassword) {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = description, tint = TextLabel)
                    }
                }
            }
        )
    }
}