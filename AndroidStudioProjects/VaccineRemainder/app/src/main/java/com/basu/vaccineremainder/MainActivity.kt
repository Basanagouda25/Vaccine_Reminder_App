package com.basu.vaccineremainder

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
import com.basu.vaccineremainder.features.navigation.AppNavGraph
import com.basu.vaccineremainder.features.navigation.NavRoutes
import com.basu.vaccineremainder.features.notifications.NotificationHelper
import com.basu.vaccineremainder.features.notifications.ReminderWorker
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
            ExistingPeriodicWorkPolicy.UPDATE,
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

    val sharedPref = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    val isLoggedIn = sharedPref.getBoolean("logged_in", false)

    val startDestination = if (isLoggedIn) {
        NavRoutes.Dashboard.route
    } else {
        NavRoutes.Login.route
    }

    MaterialTheme {
        AppNavGraph(navController = navController, startDestination = startDestination)
    }
}
