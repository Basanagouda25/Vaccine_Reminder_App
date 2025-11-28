import com.basu.vaccineremainder.features.navigation.NavRoutes

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.basu.vaccineremainder.data.database.AppDatabaseProvider
import com.basu.vaccineremainder.data.repository.AppRepository
import com.basu.vaccineremainder.features.auth.AuthViewModel
import com.basu.vaccineremainder.features.auth.AuthViewModelFactory
import com.basu.vaccineremainder.features.auth.LoginScreen
import com.basu.vaccineremainder.features.auth.ProviderAuthViewModel
import com.basu.vaccineremainder.features.auth.ProviderAuthViewModelFactory
import com.basu.vaccineremainder.features.auth.RegisterScreen
import com.basu.vaccineremainder.features.auth.RoleSelectionScreen
import com.basu.vaccineremainder.features.childprofile.AddChildScreen
import com.basu.vaccineremainder.features.childprofile.ChildDetailsScreen
import com.basu.vaccineremainder.features.childprofile.ChildListScreen
import com.basu.vaccineremainder.features.dashboard.DashboardScreen
import com.basu.vaccineremainder.features.notifications.NotificationScreen
import com.basu.vaccineremainder.features.provider.ProviderDashboardScreen
import com.basu.vaccineremainder.features.provider.ProviderLoginScreen
import com.basu.vaccineremainder.features.provider.ProviderRegistrationScreen
import com.basu.vaccineremainder.features.provider.ProviderSendNotificationScreen
import com.basu.vaccineremainder.features.schedule.ChildScheduleScreen
import com.basu.vaccineremainder.features.schedule.VaccineListScreen
import com.basu.vaccineremainder.util.SessionManager

// ... rest of the file

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(navController: NavHostController, startDestination: String) {

    // ... (your existing code for context, repository, viewmodels is correct)
    val context = LocalContext.current
    val activity = context as ComponentActivity
    val db = AppDatabaseProvider.getDatabase(context)
    val repository = AppRepository(
        db.userDao(),
        db.childDao(),
        db.vaccineDao(),
        db.scheduleDao(),
        db.notificationDao(),
        db.providerDao(),
    )
    val viewModel = ViewModelProvider(
        activity,
        AuthViewModelFactory(repository)
    )[AuthViewModel::class.java]
    val providerAuthViewModel = ViewModelProvider(
        activity,
        ProviderAuthViewModelFactory(repository)
    )[ProviderAuthViewModel::class.java]


    // --- START REPLACEMENT HERE ---
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // --- ROLE SELECTION ---
        composable(NavRoutes.RoleSelection.route) {
            RoleSelectionScreen(
                onUserClick = { navController.navigate(NavRoutes.Login.route) },
                onProviderClick = { navController.navigate(NavRoutes.ProviderLogin.route) }
            )
        }

        // --- PARENT/USER AUTH FLOW ---
        composable(NavRoutes.Login.route) {
            LoginScreen(
                viewModel = viewModel,
                onLoginResult = { user ->
                    SessionManager.login(context, SessionManager.ROLE_PARENT, user.userId)
                    // ** THE FIX IS HERE **
                    navController.navigate(NavRoutes.Dashboard.route) {
                        // Clear the entire stack to provide a clean slate for the new session
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(NavRoutes.Register.route)
                },
                onBack = { navController.popBackStack() }
            )
        }


        composable(NavRoutes.Register.route) {
            RegisterScreen(
                viewModel = viewModel,
                onRegisterSuccess = {
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }


        // --- PROVIDER AUTH FLOW ---
        composable(NavRoutes.ProviderLogin.route) {
            ProviderLoginScreen(
                repository = repository,
                viewModel = providerAuthViewModel,
                onLoginSuccess = {
                    // Symmetrical fix for provider login
                    navController.navigate(NavRoutes.ProviderDashboard.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(NavRoutes.ProviderRegister.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.ProviderRegister.route) {
            ProviderRegistrationScreen(
                viewModel = providerAuthViewModel,
                onRegisterSuccess = { navController.popBackStack() },
                onBack = { navController.popBackStack() },
                onLoginClick = { navController.popBackStack() }
            )
        }

        // --- DASHBOARDS ---
        composable(NavRoutes.Dashboard.route) {
            DashboardScreen(
                onAddChildClick = { navController.navigate(NavRoutes.AddChild.route) },
                onChildListClick = { navController.navigate(NavRoutes.ChildList.route) },
                onVaccineScheduleClick = { navController.navigate(NavRoutes.VaccineList.route) },
                onNotificationClick = { navController.navigate(NavRoutes.Notifications.route) },
                onLogoutClick = {
                    // 1. Reset the ViewModel's state (the step we just confirmed)
                    viewModel.onLogout()

                    // 2. Clear the saved session from disk
                    SessionManager.logout(context)

                    // 3. Reset the navigation graph to the very beginning
                    navController.navigate(NavRoutes.RoleSelection.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(NavRoutes.ProviderDashboard.route) {
            ProviderDashboardScreen(
                viewModel = providerAuthViewModel,
                onSendNotificationClick = {
                    navController.navigate(NavRoutes.ProviderSendNotification.route)
                },
                onViewChildrenClick = { /* TODO */ },
                onLogoutClick = {
                    // Symmetrical fix for provider logout
                    SessionManager.logout(context)
                    navController.navigate(NavRoutes.RoleSelection.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        // --- OTHER SCREENS (No changes needed here, but included for completeness) ---

        composable(NavRoutes.Notifications.route) {
            NotificationScreen(
                repository = repository,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.ProviderSendNotification.route) {
            ProviderSendNotificationScreen(
                repository = repository,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.AddChild.route) {
            val parentId = SessionManager.getCurrentUserId(context)
            AddChildScreen(
                repository = repository,
                parentId = parentId,
                onChildAdded = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.ChildList.route) {
            val parentId = SessionManager.getCurrentUserId(context)
            ChildListScreen(
                repository = repository,
                parentId = parentId,
                onChildSelected = { childId ->
                    navController.navigate("${NavRoutes.ChildDetails.route}/$childId")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("${NavRoutes.ChildDetails.route}/{childId}") { backStackEntry ->
            val childId = backStackEntry.arguments?.getString("childId")?.toInt() ?: 0
            ChildDetailsScreen(
                repository = repository,
                childId = childId,
                onBack = { navController.popBackStack() },
                onViewSchedule = { navController.navigate(NavRoutes.ChildSchedule.createRoute(childId)) }
            )
        }

        composable(NavRoutes.ChildSchedule.route) { backStackEntry ->
            val childId = backStackEntry.arguments?.getString("childId")?.toInt() ?: -1
            ChildScheduleScreen(
                repository = repository,
                childId = childId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.VaccineList.route) {
            VaccineListScreen(
                repository = repository,
                onVaccineSelected = { vaccineId ->
                    navController.navigate("${NavRoutes.VaccineDetails.route}/$vaccineId")
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("${NavRoutes.VaccineDetails.route}/{vaccineId}") { /* ... */ }
    }
}

