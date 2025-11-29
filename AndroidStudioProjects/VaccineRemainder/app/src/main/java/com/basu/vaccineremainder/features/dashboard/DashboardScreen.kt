package com.basu.vaccineremainder.features.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.basu.vaccineremainder.features.dashboardimport.UserViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- Uniform Color Palette ---
private val SlateDark = Color(0xFF556080)
private val CardGradientStart = Color(0xFFEC4899) // Pink/Orange for Parents
private val CardGradientEnd = Color(0xFFF97316)   // Orange
private val SurfaceBg = Color(0xFFF1F5F9)
private val TextDark = Color(0xFF334155)
private val IconBgBlue = Color(0xFFE2E8F0)
private val IconTintBlue = Color(0xFF64748B)

@Composable
fun DashboardScreen(
    // --- FIX 1: Add UserViewModel ---
    viewModel: UserViewModel,
    onAddChildClick: () -> Unit,
    onChildListClick: () -> Unit,
    onVaccineScheduleClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    // --- FIX 2: Get data from ViewModel ---
    val user by viewModel.user.collectAsState()
    val children by viewModel.children.collectAsState()

    // --- FIX 3: Find the next due vaccine (simple example) ---
    // This is a placeholder. A real implementation would query the database for upcoming vaccines.
    val nextChildToVaccinate = children.firstOrNull() // For now, just take the first child
    val nextDueDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(System.currentTimeMillis() + 86400000 * 7)) // Mock: 7 days from now

    Scaffold(
        bottomBar = { ParentBottomNavBar() },
        containerColor = SlateDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // --- 1. Header Section ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face, // Using Face icon for parent
                            contentDescription = "Profile",
                            tint = SlateDark
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            // --- FIX 4: Use dynamic user name ---
                            text = "Hi, ${user?.name ?: "Parent"}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = "Welcome Back",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }

                Text(
                    text = "LOGOUT",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.clickable { onLogoutClick() }
                )
            }

            // --- 2. Sliding Surface ---
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = SurfaceBg
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {

                    // --- 3. Parent Hero Card (Next Vaccine) ---
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(CardGradientStart, CardGradientEnd)
                                )
                            )
                    ) {
                        WavePattern() // Reusing the same wave pattern

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Upcoming Vaccine", color = Color.White.copy(alpha = 0.9f))
                                Text("DUE SOON", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.8f))
                            }

                            Column {
                                Text(
                                    // --- FIX 5: Use dynamic vaccine data ---
                                    text = nextChildToVaccinate?.let { "Polio (OPV-1)" } ?: "No upcoming vaccines",
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    // --- FIX 6: Use dynamic child name ---
                                    text = nextChildToVaccinate?.let { "For: ${it.name}" } ?: "Add a child to see schedule",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Due Date", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                                    // --- FIX 7: Use dynamic due date ---
                                    Text(
                                        text = nextChildToVaccinate?.let { nextDueDate } ?: "N/A",
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- 4. Quick Actions Grid ---
                    Text(
                        text = "Manage Family",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextDark,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    val actions = listOf(
                        DashboardActionItem("Add Child", "Register New", Icons.Default.Add, onAddChildClick),
                        DashboardActionItem("My Children", "View List", Icons.Default.ChildCare, onChildListClick),
                        DashboardActionItem("Schedule", "Vaccine Timeline", Icons.Default.DateRange, onVaccineScheduleClick),
                        DashboardActionItem("Alerts", "Notifications", Icons.Default.Notifications, onNotificationClick)
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(actions) { action ->
                            ActionCard(action)
                        }
                    }
                }
            }
        }
    }
}

// --- Reusable Components (Same as Provider Screen) ---
// Your reusable components are great, no changes needed here.
data class DashboardActionItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun ActionCard(action: DashboardActionItem) {
    Surface(
        onClick = action.onClick,
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(IconBgBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = null,
                    tint = IconTintBlue,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = action.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = TextDark
            )
            Text(
                text = action.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ParentBottomNavBar() {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            selected = true,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent, selectedIconColor = SlateDark, unselectedIconColor = Color.Gray),
            label = { Text("Home", fontSize = 10.sp, color = SlateDark) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.CalendarMonth, contentDescription = "Schedule") },
            selected = false,
            onClick = { /* TODO: Navigate to Schedule Screen */ },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray),
            label = { Text("Schedule", fontSize = 10.sp, color = Color.Gray) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Person, contentDescription = "Profile") },
            selected = false,
            onClick = { /* TODO: Navigate to Profile Screen */ },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray),
            label = { Text("Profile", fontSize = 10.sp, color = Color.Gray) }
        )
    }
}

// Reuse WavePattern from ProviderDashboardScreen file or copy it here if separate file
@Composable
fun WavePattern() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val path = Path().apply {
            moveTo(0f, height * 0.7f)
            cubicTo(width * 0.2f, height * 0.6f, width * 0.3f, height * 0.9f, width * 0.5f, height * 0.8f)
            cubicTo(width * 0.7f, height * 0.7f, width * 0.8f, height * 0.95f, width, height * 0.85f)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = path,
            color = Color.White.copy(alpha = 0.1f)
        )
    }
}
