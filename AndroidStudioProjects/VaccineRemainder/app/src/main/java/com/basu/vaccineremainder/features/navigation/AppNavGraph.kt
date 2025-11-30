package com.basu.vaccineremainder.features.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.basu.vaccineremainder.data.database.AppDatabaseProvider
import com.basu.vaccineremainder.data.repository.AppRepository
import com.basu.vaccineremainder.features.auth.*
import com.basu.vaccineremainder.features.childprofile.AddChildScreen
import com.basu.vaccineremainder.features.childprofile.AddChildViewModel
import com.basu.vaccineremainder.features.childprofile.AddChildViewModelFactory
import com.basu.vaccineremainder.features.childprofile.ChildDetailsScreen
import com.basu.vaccineremainder.features.childprofile.ChildListScreen
import com.basu.vaccineremainder.features.dashboard.DashboardScreen
import com.basu.vaccineremainder.features.dashboardimport.UserViewModel
import com.basu.vaccineremainder.features.dashboardimport.UserViewModelFactory
import com.basu.vaccineremainder.features.notifications.NotificationScreen
import com.basu.vaccineremainder.features.provider.*
import com.basu.vaccineremainder.features.schedule.ChildScheduleScreen
import com.basu.vaccineremainder.features.schedule.VaccineListScreen
import com.basu.vaccineremainder.util.SessionManager
import com.google.firebase.auth.FirebaseAuth

// NOTE: We are removing the UserViewModel imports as they are not used in the original structure.

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(navController: NavHostController, startDestination: String) {

    val context = LocalContext.current
    val db = AppDatabaseProvider.getDatabase(context)
    val repository = AppRepository(
        db.userDao(),
        db.childDao(),
        db.vaccineDao(),
        db.scheduleDao(),
        db.notificationDao(),
        db.providerDao(),
    )

    // We only need the ViewModels that are actually being used
    val authViewModel: AuthViewModel = viewModel()
    val providerAuthViewModel: ProviderAuthViewModel = viewModel(factory = ProviderAuthViewModelFactory(repository))

    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(repository))

    val addChildViewModel: AddChildViewModel = viewModel(
        factory = AddChildViewModelFactory(
            repository,
            FirebaseAuth.getInstance()
        )
    )


    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // --- AUTH & PROVIDER FLOW (This part was correct and remains unchanged) ---
        composable(NavRoutes.RoleSelection.route) {
            RoleSelectionScreen(
                onUserClick = { navController.navigate(NavRoutes.Login.route) },
                onProviderClick = { navController.navigate(NavRoutes.ProviderLogin.route) }
            )
        }
        composable(NavRoutes.Login.route) {
            LoginScreen(
                viewModel = authViewModel,

                onLoginResult = { user ->
                    SessionManager.login(context, SessionManager.ROLE_PARENT, user.userId, email = user.email)
                    //providerAuthViewModel.startObservingChildren()
                    navController.navigate(NavRoutes.Dashboard.route) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToRegister = { navController.navigate(NavRoutes.Register.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(NavRoutes.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = { navController.popBackStack() },
                onNavigateToLogin = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
        composable(NavRoutes.ProviderLogin.route) {
            ProviderLoginScreen(
                viewModel = providerAuthViewModel,
                onLoginSuccess = { provider ->
                    SessionManager.login(context, SessionManager.ROLE_PROVIDER, provider.providerId, email = provider.email)

                    // This now WAITS for the data to load before continuing
                    providerAuthViewModel.loadProviderData()

                    // This line will only run AFTER the data is loaded
                    navController.navigate(NavRoutes.ProviderDashboard.route) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToRegister = { navController.navigate(NavRoutes.ProviderRegister.route) },
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

        composable(NavRoutes.Dashboard.route) {
            DashboardScreen(
                repository = repository,
                auth = FirebaseAuth.getInstance(),
                onAddChildClick = { navController.navigate(NavRoutes.AddChild.route) },
                onChildListClick = { navController.navigate(NavRoutes.ChildList.route) },
                onVaccineScheduleClick = {navController.navigate(NavRoutes.VaccineList.route)  },
                onNotificationClick = { navController.navigate(NavRoutes.Notifications.route) },
                onLogoutClick = {
                    FirebaseAuth.getInstance().signOut()
                    SessionManager.logout(context)

                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }




        // --- AddChildScreen was also using the repository directly in your original code ---
        composable(NavRoutes.AddChild.route) {
            val parentId = SessionManager.getCurrentUserId(context)
            val parentEmail = SessionManager.getParentEmail(context) ?: ""
            AddChildScreen(
                viewModel = addChildViewModel, // Pass the correct ViewModel
                parentId = parentId,
                parentEmail = parentEmail,
                onChildAdded = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.ChildList.route) {
            val parentEmail = SessionManager.getParentEmail(context)
            ChildListScreen(
                repository = repository,
                parentEmail = parentEmail,
                onChildSelected = { childId ->
                    navController.navigate("${NavRoutes.ChildDetails.route}/$childId")
                },
                onBack = { navController.popBackStack() }
            )
        }


        // --- FIX: REVERT ChildDetailsScreen to use repository and childId ---
        composable("${NavRoutes.ChildDetails.route}/{childId}") { backStackEntry ->
            val childId = backStackEntry.arguments?.getString("childId")?.toLong() ?: 0L
            ChildDetailsScreen(
                repository = repository, // REVERTED
                childId = childId,
                onBack = { navController.popBackStack() },
                // TO (Build the route directly as a string):
                onViewSchedule = { navController.navigate("${NavRoutes.ChildSchedule.route}/$childId") }

            )
        }

        // --- THIS IS THE FINAL, CORRECT BLOCK ---
        composable(
            route = "${NavRoutes.ChildSchedule.route}/{childId}",
            arguments = listOf(navArgument("childId") { type = NavType.StringType })
        ) { backStackEntry ->

            // --- THIS IS THE MISSING LINE ---
            val childId = backStackEntry.arguments?.getString("childId")?.toLong() ?: 0L

            // Now the `childId` variable exists and has the correct Long value.
            ChildScheduleScreen(
                repository = repository,
                childId = childId,
                onBack = { navController.popBackStack() }
            )
        }


        // --- PROVIDER DASHBOARD (This part is correct) ---
        composable(NavRoutes.ProviderDashboard.route) {
            ProviderDashboardScreen(
                viewModel = providerAuthViewModel,
                onLogoutClick = {
                    SessionManager.logout(context)
                    navController.navigate(NavRoutes.RoleSelection.route) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onSendNotificationClick = { navController.navigate(NavRoutes.ProviderSendNotification.route) },
                onAddPatientClick = {  },
                onViewChildrenClick = { navController.navigate(NavRoutes.ViewPatients.route) }
            )
        }
        composable(NavRoutes.ViewPatients.route) {
            ViewPatientsScreen(
                viewModel = providerAuthViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(NavRoutes.ProviderSendNotification.route) {
            ProviderSendNotificationScreen(
                viewModel = providerAuthViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // --- OTHER SCREENS (These are fine) ---
        composable(NavRoutes.Notifications.route) {
            NotificationScreen(
                repository = repository,
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
