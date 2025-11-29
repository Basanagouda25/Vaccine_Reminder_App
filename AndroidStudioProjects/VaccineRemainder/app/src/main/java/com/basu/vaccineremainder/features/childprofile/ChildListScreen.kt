package com.basu.vaccineremainder.features.childprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.basu.vaccineremainder.data.model.Child
import com.basu.vaccineremainder.data.repository.AppRepository
import kotlinx.coroutines.flow.Flow

// --- Uniform Color Palette ---
private val SlateDark = Color(0xFF556080)    // Premium Header
private val TextHead = Color(0xFF0F172A)
private val TextLabel = Color(0xFF334155)
private val TextGrey = Color(0xFF64748B)
private val IconBgBlue = Color(0xFFE2E8F0)
private val IconTintBlue = Color(0xFF64748B)
private val SurfaceBg = Color(0xFFF1F5F9)

@Composable
fun ChildListScreen(
    repository: AppRepository,
    parentId: Int,
    onChildSelected: (Int) -> Unit,
    onBack: () -> Unit
) {
    var childList by remember { mutableStateOf<List<Child>>(emptyList()) }

    // Logic: Fetch children based on parentId
    LaunchedEffect(parentId) {
        val childrenFlow: Flow<List<Child>> = if (parentId == -1) {
            repository.getAllChildren()
        } else {
            repository.getChildrenByParentId(parentId)
        }

        childrenFlow.collect { list ->
            childList = list
        }
    }

    // --- Root Container (Dark Background) ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // --- 1. Header Section ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Back Button
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBack() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Header Content
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Icon Container
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (parentId == -1) Icons.Outlined.Person else Icons.Outlined.ChildCare,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = if (parentId == -1) "All Patients" else "Your Children",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    Text(
                        text = "${childList.size} Registered",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- 2. Sliding Surface (List Area) ---
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = SurfaceBg // Light Grey background for list contrast
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                if (childList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No records found.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextGrey
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(childList) { child ->
                            ChildListItem(child = child, onChildSelected = onChildSelected)
                        }
                    }
                }
            }
        }
    }
}

// --- Reusable List Item (Card Style) ---
@Composable
fun ChildListItem(child: Child, onChildSelected: (Int) -> Unit) {
    Surface(
        onClick = { onChildSelected(child.childId) },
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 0.dp, // Flat modern style
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar / Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(IconBgBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = null,
                    tint = IconTintBlue,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Details Column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = child.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextHead
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "DOB: ${child.dateOfBirth}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGrey
                )
                Text(
                    text = "Gender: ${child.gender}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGrey
                )
            }

            // Arrow Icon
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Select",
                tint = TextGrey.copy(alpha = 0.5f)
            )
        }
    }
}