package com.basu.vaccineremainder

import AppNavGraph
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import androidx.work.*
import com.basu.vaccineremainder.features.navigation.NavRoutes
import com.basu.vaccineremainder.features.notifications.NotificationHelper
import com.basu.vaccineremainder.features.notifications.ReminderWorker
import com.basu.vaccineremainder.util.SessionManager // <-- Make sure this is imported
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotificationHelper.createNotificationChannel(this)
        requestNotificationPermission()

        val request = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "vaccine_reminder_work",
            ExistingPeriodicWorkPolicy.KEEP, // Use KEEP instead of UPDATE for periodic work
            request
        )

        setContent {
            VaccineReminderApp()
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VaccineReminderApp() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // --- THIS IS THE CRITICAL FIX ---
    // Check the logged-in status from SessionManager to determine the starting screen.
    val startDestination = when (SessionManager.getLoggedInRole(context)) {
        SessionManager.ROLE_PARENT -> NavRoutes.Dashboard.route
        SessionManager.ROLE_PROVIDER -> NavRoutes.ProviderDashboard.route
        else -> NavRoutes.RoleSelection.route // If no one is logged in, start at role selection.
    }
    // --------------------------------

    MaterialTheme {
        AppNavGraph(navController = navController, startDestination = startDestination)
    }
}
