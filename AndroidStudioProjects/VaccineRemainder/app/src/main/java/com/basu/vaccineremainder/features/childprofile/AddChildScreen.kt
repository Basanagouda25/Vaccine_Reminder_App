package com.basu.vaccineremainder.features.childprofile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.basu.vaccineremainder.data.model.Child
import com.basu.vaccineremainder.data.repository.AppRepository
import kotlinx.coroutines.launch
import java.util.Calendar
import android.app.DatePickerDialog

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddChildScreen(
    repository: AppRepository,
    parentId: Int,
    onChildAdded: () -> Unit
) {
    var childName by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // --- DATE PICKER DIALOG ---
    if (showDatePicker) {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            context,
            { _, year, month, day ->
                val formatted = "%04d-%02d-%02d".format(year, month + 1, day)
                dateOfBirth = formatted   // set selected date
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
    ) {
        Text("Add Child", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(18.dp))

        // Child Name
        OutlinedTextField(
            value = childName,
            onValueChange = { childName = it },
            label = { Text("Child Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // DOB (opens date picker)
        OutlinedTextField(
            value = dateOfBirth,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            label = { Text("Date of Birth (Tap to Select)") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Gender
        OutlinedTextField(
            value = gender,
            onValueChange = { gender = it },
            label = { Text("Gender") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Save Button
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                scope.launch {
                    val child = Child(
                        parentId = parentId,
                        name = childName,
                        dateOfBirth = dateOfBirth,  // already yyyy-MM-dd
                        gender = gender
                    )

                    repository.insertChild(child)

                    val insertedChild = repository.getChildrenByParentId(parentId).lastOrNull()

                    insertedChild?.let {
                        repository.generateScheduleForChild(
                            it.childId,
                            it.dateOfBirth
                        )
                    }

                    onChildAdded()
                }
            }
        ) {
            Text("Save Child")
        }
    }
}
