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
    onChildSelected: (Int) -> Unit
) {
    var childrenList by remember { mutableStateOf<List<Child>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            childrenList = repository.getChildrenByParentId(parentId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Children List",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (childrenList.isEmpty()) {
            Text("No children added yet.")
        } else {
            LazyColumn {
                items(childrenList) { child ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { onChildSelected(child.childId) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = child.name, style = MaterialTheme.typography.titleMedium)
                            Text(text = "DOB: ${child.dateOfBirth}")
                            Text(text = "Gender: ${child.gender}")
                        }
                    }
                }
            }
        }
    }
}
