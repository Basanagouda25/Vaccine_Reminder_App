package com.basu.vaccineremainder.features.provider

import android.widget.Toast
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.basu.vaccineremainder.features.auth.ProviderAuthViewModel

// --- Color Palette ---
private val SlateDark = Color(0xFF556080)
private val CardGradientStart = Color(0xFF8DA4C3)
private val CardGradientEnd = Color(0xFF607090)
private val SurfaceBg = Color(0xFFF1F5F9)
private val TextDark = Color(0xFF334155)
private val IconBgBlue = Color(0xFFE2E8F0)
private val IconTintBlue = Color(0xFF64748B)

@Composable
fun ProviderDashboardScreen(
    viewModel: ProviderAuthViewModel,
    onViewChildrenClick: () -> Unit,
    onSendNotificationClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onAddPatientClick: () -> Unit = {}
) {
    val children by viewModel.childrenList.collectAsState()
    val provider by viewModel.providerState.collectAsState()
    val context = LocalContext.current

    // --- THIS IS THE FIX ---
    // This effect will run when 'provider' is no longer null.
    // It triggers the function to fetch the list of children for this provider.
    LaunchedEffect(provider) {
        provider?.providerId?.let { id ->
            viewModel.loadAllChildren()
        }
    }
    // ------------------------

    Scaffold(
        bottomBar = { CustomBottomNavBar() },
        containerColor = SlateDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header Section
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
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = SlateDark
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Hi, ${provider?.name ?: "Provider"}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = provider?.clinicName ?: "City Health Clinic",
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

            // Main Content Surface
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
                    // Hero Card
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
                        WavePattern()
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
                                Text("Clinic Status", color = Color.White.copy(alpha = 0.8f))
                                Text("ACTIVE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.6f))
                            }
                            Column {
                                // Dynamic Patient Count from the ViewModel state
                                Text(
                                    text = children.size.toString(),
                                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    letterSpacing = 2.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Total Patients Registered",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Next Alert", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
                                    Text("03 / 10", fontWeight = FontWeight.SemiBold, color = Color.White)
                                }
                                Icon(
                                    imageVector = Icons.Default.SignalCellularAlt,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Quick Actions Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Quick Actions",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextDark
                        )
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = "More",
                            tint = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val actions = listOf(
                        DashboardActionItem("View Children", "Manage Records", Icons.Default.ChildCare, onViewChildrenClick),
                        DashboardActionItem("Send Alert", "Notify Parents", Icons.Default.NotificationsActive, onSendNotificationClick),
                        DashboardActionItem("Add Patient", "Registration", Icons.Default.PersonAdd, onAddPatientClick),
                        DashboardActionItem("History", "Past Alerts", Icons.Default.History) {
                            Toast.makeText(context, "History feature coming soon!", Toast.LENGTH_SHORT).show()
                        }
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

// All other components (ActionCard, CustomBottomNavBar, WavePattern, etc.) remain unchanged.
// ... (paste the rest of your original code here) ...
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
fun CustomBottomNavBar() {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            selected = true,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent,
                selectedIconColor = SlateDark,
                unselectedIconColor = Color.Gray
            ),
            label = { Text("Home", fontSize = 10.sp, color = SlateDark) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.CreditCard, contentDescription = "Cards") },
            selected = false,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray),
            label = { Text("Cards", fontSize = 10.sp, color = Color.Gray) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Analytics, contentDescription = "Stats") },
            selected = false,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray),
            label = { Text("Stats", fontSize = 10.sp, color = Color.Gray) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Person, contentDescription = "Profile") },
            selected = false,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray),
            label = { Text("Profile", fontSize = 10.sp, color = Color.Gray) }
        )
    }
}

@Composable
fun WavePattern() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val path = Path().apply {
            moveTo(0f, height * 0.4f)
            cubicTo(
                width * 0.2f, height * 0.3f,
                width * 0.5f, height * 0.6f,
                width, height * 0.4f
            )
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        val path2 = Path().apply {
            moveTo(0f, height * 0.6f)
            cubicTo(
                width * 0.4f, height * 0.4f,
                width * 0.7f, height * 0.8f,
                width, height * 0.5f
            )
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = path,
            color = Color.White.copy(alpha = 0.05f)
        )
        drawPath(
            path = path2,
            color = Color.White.copy(alpha = 0.05f)
        )
        drawPath(
            path = path,
            color = Color.White.copy(alpha = 0.1f),
            style = Stroke(width = 2f)
        )
    }
}
