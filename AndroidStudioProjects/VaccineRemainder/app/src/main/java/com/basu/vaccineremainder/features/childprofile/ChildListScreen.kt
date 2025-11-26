package com.basu.vaccineremainder.features.childprofile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.basu.vaccineremainder.data.model.Child
import com.basu.vaccineremainder.data.repository.AppRepository
import kotlinx.coroutines.launch

@Composable
fun ChildListScreen(
    repository: AppRepository,
    parentId: Int,
    onChildSelected: (Int) -> Unit,
    onBack: () -> Unit   // ⭐ Add this callback
) {
    var childList by remember { mutableStateOf<List<Child>>(emptyList()) }

    LaunchedEffect(parentId) {
        childList = repository.getChildrenByParentId(parentId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // ⭐ BACK BUTTON
        Button(onClick = { onBack() }) {
            Text("⬅ Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your Children",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (childList.isEmpty()) {
            Text("No children added yet.")
        } else {
            childList.forEach { child ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { onChildSelected(child.childId) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Name: ${child.name}")
                        Text("DOB: ${child.dateOfBirth}")
                        Text("Gender: ${child.gender}")
                    }
                }
            }
        }
    }
}

