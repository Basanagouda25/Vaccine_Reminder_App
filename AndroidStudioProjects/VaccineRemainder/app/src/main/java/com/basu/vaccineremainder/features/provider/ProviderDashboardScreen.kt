package com.basu.vaccineremainder.features.provider

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
private val TextLabel = Color(0xFF64748B)
private val IconBgBlue = Color(0xFFE2E8F0)
private val IconTintBlue = Color(0xFF64748B)

@Composable
fun ProviderDashboardScreen(
    viewModel: ProviderAuthViewModel,
    onViewChildrenClick: () -> Unit,
    onSendNotificationClick: () -> Unit,
    onVaccineCatalogClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onFaqClick: () -> Unit,
    onProviderProfileClick: () -> Unit,
    onAddPatientClick: () -> Unit = {},
    // Added Learn callback to match logic
    onLearnClick: () -> Unit = {}
) {
    val children by viewModel.childrenList.collectAsState()

    val provider by viewModel.providerState.collectAsState()
    val context = LocalContext.current

    println("Dashboard Debug: Found ${children.size} children")
    LaunchedEffect(Unit) {
        // Always call this; the ViewModel will handle checking the Firebase Auth user
        viewModel.loadProviderData()
    }

    Scaffold(
        bottomBar = {
            CustomBottomNavBar(
                onFaqClick = onFaqClick,
                onProviderProfileClick = onProviderProfileClick,
                onLearnClick = onLearnClick
            )
        },
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

            // --- 2. Sliding Surface ---
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = SurfaceBg
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    // --- Hero Card (Stats) ---
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
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
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- Primary Actions (Vertical List) ---
                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextDark,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // 1. Patient Directory
                    LargeActionCard(
                        title = "Patient Directory",
                        subtitle = "View and manage registered children",
                        icon = Icons.Default.ChildCare,
                        onClick = onViewChildrenClick
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. Send Notification
                    LargeActionCard(
                        title = "Send Notification",
                        subtitle = "Broadcast alerts to parents",
                        icon = Icons.Default.NotificationsActive,
                        onClick = onSendNotificationClick
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. Vaccine Catalog
                    LargeActionCard(
                        title = "Vaccine Catalog",
                        subtitle = "View list of all available vaccines",
                        icon = Icons.Default.Vaccines,
                        onClick = onVaccineCatalogClick
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

// --- Helper Components (Private to avoid conflicts) ---

@Composable
private fun LargeActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(IconBgBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = IconTintBlue,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextLabel
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextLabel.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun CustomBottomNavBar(
    onFaqClick: () -> Unit,
    onProviderProfileClick: () -> Unit,
    onLearnClick: () -> Unit
) {
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


        // Profile
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Person, contentDescription = "Profile") },
            selected = false,
            onClick = onProviderProfileClick,
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray),
            label = { Text("Profile", fontSize = 10.sp, color = Color.Gray) }
        )
    }
}

@Composable
private fun WavePattern() {
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