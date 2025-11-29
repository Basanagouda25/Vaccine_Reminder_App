package com.basu.vaccineremainder.features.childprofile

import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.basu.vaccineremainder.data.model.Child
import java.util.Calendar

// --- Uniform Color Palette ---
private val SlateDark = Color(0xFF556080)
private val PrimaryIndigo = Color(0xFF4F46E5)
private val TextHead = Color(0xFF0F172A)
private val TextLabel = Color(0xFF334155)
private val TextPlaceholder = Color(0xFF94A3B8)
private val InputBorder = Color(0xFFE2E8F0)
private val InputBg = Color(0xFFF8FAFC)


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddChildScreen(
    viewModel: AddChildViewModel,
    parentId: Int,
    parentEmail: String,
    onChildAdded: () -> Unit,
    onBack: () -> Unit
) {
    // --- State variables for the form ---
    var childName by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // --- DATE PICKER DIALOG ---
    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                dateOfBirth = "%04d-%02d-%02d".format(year, month + 1, day)
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setOnDismissListener { showDatePicker = false }
            show()
        }
    }

    // --- UI Structure ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark)
    ) {
        // --- Header Section ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
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
                    imageVector = Icons.Outlined.ChildCare,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Add Child",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = Color.White
            )
            Text(
                text = "Enter details to track vaccinations.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // --- Form Area ---
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 32.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                AddChildTextField(
                    label = "Child Name",
                    value = childName,
                    onValueChange = { childName = it },
                    placeholder = "Baby Name"
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Date Picker Field
                Text(
                    text = "Date of Birth",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = TextLabel,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
                OutlinedTextField(
                    value = dateOfBirth,
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    enabled = false,
                    readOnly = true,
                    shape = RoundedCornerShape(16.dp),
                    placeholder = { Text("YYYY-MM-DD", color = TextPlaceholder) },
                    trailingIcon = {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = "Select Date",
                            tint = PrimaryIndigo,
                            modifier = Modifier.clickable { showDatePicker = true }
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = TextHead,
                        disabledBorderColor = InputBorder,
                        disabledContainerColor = InputBg,
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                AddChildTextField(
                    label = "Gender",
                    value = gender,
                    onValueChange = { gender = it },
                    placeholder = "Male / Female"
                )
                Spacer(modifier = Modifier.height(32.dp))

                // --- Save Button ---
                Button(
                    onClick = {
                        isLoading = true
                        // --- FIX: Create Child object without providerId ---
                        val child = Child(
                            parentId = parentId,
                            parentEmail = parentEmail,
                            name = childName,
                            dateOfBirth = dateOfBirth,
                            gender = gender,
                            providerId = null // Set providerId to null
                        )
                        viewModel.addChild(child)
                        isLoading = false
                        onChildAdded()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                    enabled = !isLoading && childName.isNotBlank() && dateOfBirth.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Save Child",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// Reusable TextField (No changes needed)
@Composable
private fun AddChildTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = TextLabel,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
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
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true
        )
    }
}
