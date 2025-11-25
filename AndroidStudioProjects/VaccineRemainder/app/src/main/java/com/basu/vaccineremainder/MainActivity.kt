package com.basu.vaccineremainder

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.basu.vaccineremainder.features.navigation.AppNavGraph
import com.basu.vaccineremainder.features.navigation.NavRoutes

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            VaccineReminderApp()
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

