package com.basu.vaccineremainder.features.childprofile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.basu.vaccineremainder.data.model.Child
import com.basu.vaccineremainder.data.repository.AppRepository
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildListScreen(
    repository: AppRepository,
    parentId: Int,
    onChildSelected: (Int) -> Unit,
    onBack: () -> Unit
) {
    var childList by remember { mutableStateOf<List<Child>>(emptyList()) }

    // This effect will run when `parentId` changes
    LaunchedEffect(parentId) {
        val childrenFlow: Flow<List<Child>> = if (parentId == -1) {
            // PROVIDER is viewing: Get all children
            repository.getAllChildren()
        } else {
            // PARENT is viewing: Get their own children using the correct function name
            repository.getChildrenByParentId(parentId)
        }

        childrenFlow.collect { list ->
            childList = list
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Change the title based on who is viewing
                    Text(if (parentId == -1) "All Children" else "Your Children")
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        // Using a standard icon for the back button
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (childList.isEmpty()) {
                Text("No children found.")
            } else {
                // Use LazyColumn for better performance with lists
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(childList) { child ->
                        ChildListItem(child = child, onChildSelected = onChildSelected)
                    }
                }
            }
        }
    }
}

@Composable
fun ChildListItem(child: Child, onChildSelected: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onChildSelected(child.childId) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Name: ${child.name}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("DOB: ${child.dateOfBirth}", style = MaterialTheme.typography.bodyMedium)
            Text("Gender: ${child.gender}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
