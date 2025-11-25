package com.basu.vaccineremainder.features.childprofile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.basu.vaccineremainder.data.model.Child
import com.basu.vaccineremainder.data.repository.AppRepository
import kotlinx.coroutines.launch

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

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Add Child", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
            value = childName,
            onValueChange = { childName = it },
            label = { Text("Child Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = dateOfBirth,
            onValueChange = { dateOfBirth = it },
            label = { Text("Date of Birth (DD-MM-YYYY)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = gender,
            onValueChange = { gender = it },
            label = { Text("Gender") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                scope.launch {
                    val child = Child(
                        parentId = parentId,
                        name = childName,
                        dateOfBirth = dateOfBirth,  // MUST be yyyy-MM-dd
                        gender = gender
                    )

                    // Insert child
                    repository.insertChild(child)

                    // Get the newly inserted child
                    val insertedChild = repository.getChildrenByParentId(parentId).lastOrNull()

                    // Generate schedule for this child
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
